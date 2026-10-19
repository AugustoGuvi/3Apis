package org.gradle.wrapper;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Minimal educational Gradle launcher, compiled as gradle-wrapper.jar.
 * It downloads the distribution declared in gradle-wrapper.properties, then
 * forwards all command arguments to the Gradle executable.
 * This is NOT the official Gradle Wrapper jar. Source code included for review.
 */
public final class GradleWrapperMain {
    public static void main(String[] args) throws Exception {
        Path wrapperFolder = Paths.get(GradleWrapperMain.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getParent();
        Path propsFile = wrapperFolder.resolve("gradle-wrapper.properties");
        Properties config = new Properties();
        try (InputStream in = Files.newInputStream(propsFile)) {
            config.load(in);
        }
        String distributionUrl = config.getProperty("distributionUrl");
        if (distributionUrl == null || !distributionUrl.startsWith("https://services.gradle.org/distributions/gradle-")) {
            throw new IllegalStateException("Unexpected or unsafe Gradle distribution URL");
        }
        String archiveName = distributionUrl.substring(distributionUrl.lastIndexOf('/') + 1);
        String gradleDirName = archiveName.replace("-bin.zip", "").replace("-all.zip", "");
        Path cache = Paths.get(System.getProperty("user.home"), ".gradle", "wrapper", "libris-bootstrap");
        Path executable = cache.resolve(gradleDirName).resolve("bin")
                .resolve(isWindows() ? "gradle.bat" : "gradle");

        if (!Files.isRegularFile(executable)) {
            Files.createDirectories(cache);
            Path zipFile = cache.resolve(archiveName);
            if (!Files.isRegularFile(zipFile)) {
                System.out.println("Downloading " + distributionUrl);
                try (InputStream download = URI.create(distributionUrl).toURL().openStream()) {
                    Files.copy(download, zipFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            System.out.println("Extracting Gradle to " + cache);
            try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    Path extracted = cache.resolve(entry.getName()).normalize();
                    if (!extracted.startsWith(cache)) {
                        throw new IllegalStateException("Unsafe archive entry: " + entry.getName());
                    }
                    if (entry.isDirectory()) {
                        Files.createDirectories(extracted);
                    } else {
                        Files.createDirectories(extracted.getParent());
                        Files.copy(zip, extracted, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zip.closeEntry();
                }
            }
            if (!isWindows()) executable.toFile().setExecutable(true);
        }
        if (!Files.isRegularFile(executable)) throw new IllegalStateException("Gradle executable missing");
        List<String> command = new ArrayList<>();
        if (isWindows()) {
            command.add("cmd.exe");
            command.add("/c");
        }
        command.add(executable.toAbsolutePath().toString());
        command.addAll(Arrays.asList(args));
        ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO();
        processBuilder.directory(wrapperFolder.getParent().getParent().toFile());
        int code = processBuilder.start().waitFor();
        System.exit(code);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
