package uz.yozapp.core.network

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
