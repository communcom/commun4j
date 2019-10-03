/**
 * Copyright 2013-present memtrip LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.golos.commun4j.http.rpc

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.golos.commun4j.http.rpc.utils.DateAdapter
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Api(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi = Moshi.Builder().add(DateAdapter()).build(),
        converterFactory: Converter.Factory = MoshiConverterFactory.create(moshi),
        private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(converterFactory)
        .build(),
        val chain: io.golos.commun4j.http.rpc.ChainApi = retrofit.create(io.golos.commun4j.http.rpc.ChainApi::class.java),
        val history: io.golos.commun4j.http.rpc.HistoryApi = retrofit.create(io.golos.commun4j.http.rpc.HistoryApi::class.java)
)