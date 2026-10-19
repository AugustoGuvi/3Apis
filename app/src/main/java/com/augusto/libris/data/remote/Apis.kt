package com.augusto.libris.data.remote

import com.augusto.libris.data.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Las tres APIs REST, cada una con su propio servicio Retrofit. */
interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("printType") printType: String = "books"
    ): GoogleBooksResponseDto
}

interface OpenLibraryApi {
    @GET("search/authors.json")
    suspend fun searchAuthors(
        @Query("q") name: String,
        @Query("limit") limit: Int = 8
    ): AuthorSearchResponseDto

    @GET("authors/{authorId}.json")
    suspend fun getAuthor(@Path("authorId") authorId: String): AuthorDetailDto

    @GET("authors/{authorId}/works.json")
    suspend fun getWorks(
        @Path("authorId") authorId: String,
        @Query("limit") limit: Int = 16
    ): AuthorWorksResponseDto
}

interface GutendexApi {
    @GET("books/")
    suspend fun searchBooks(@Query("search") query: String): GutendexResponseDto
}
