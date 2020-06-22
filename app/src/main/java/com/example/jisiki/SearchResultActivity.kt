package com.example.jisiki

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search_result.*
import org.json.JSONObject
import java.io.*
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

//검색 결과를 뉴스 글 수로 정렬해 보여주는 액티비티.
class SearchResultActivity : AppCompatActivity() {
    var productName =""
    var readWords = ArrayList<String>()
    var index = 0
    var searchResultArray = ArrayList<SearchData>()
    lateinit var adapter: SearchResultAdapter
    var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        init()
    }

    private fun init() {
        readWords = intent.extras["readWords"] as ArrayList<String>
        productName = intent.extras["productName"] as String

        val searchTask = NaverSearchAsyncTask(this)
        searchTask.execute()

        while(!flag){
        }

        recyclerViewASR.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = SearchResultAdapter(searchResultArray)

        adapter.itemClickListener = object: SearchResultAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: SearchResultAdapter.MyViewHolder,
                view: View,
                data: SearchData,
                position: Int
            ) {
                Toast.makeText(this@SearchResultActivity, data.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        recyclerViewASR.adapter = adapter
    }

    class NaverSearchAsyncTask(context: SearchResultActivity): AsyncTask<URL, Unit, Unit>() {
        val activityreference = WeakReference(context)
        val activity = activityreference.get()

        override fun doInBackground(vararg p0: URL?) {
            for(i in 0 .. activity?.readWords?.size!! - 1) {
                val clientId = "jNZbBbG2dPcMJzZYO725"
                val clientSecret = "lfCEUF2h3a"

                var searchWord: String? = null
                var productName = activity.productName
                searchWord = try {
                    URLEncoder.encode(activity?.readWords[i], "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    throw RuntimeException("검색어 인코딩 실패", e)
                }

                val apiURL = "https://openapi.naver.com/v1/search/news.json?query=$productName%20$searchWord" // json 결과

                val requestHeaders: MutableMap<String, String> = HashMap()
                requestHeaders["X-Naver-Client-Id"] = clientId
                requestHeaders["X-Naver-Client-Secret"] = clientSecret
                val responseBody: String? = get(apiURL, requestHeaders)

                activity?.searchResultArray.add(SearchData(activity.readWords[i]!!, JSONObject(responseBody)))
            }
            activity?.flag = true
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            activity?.adapter = SearchResultAdapter(activity?.searchResultArray!!)
            activity?.recyclerViewASR.adapter = activity?.adapter

        }

        private operator fun get(
            apiUrl: String,
            requestHeaders: Map<String, String>
        ): String? {
            val con = connect(apiUrl)
            return try {
                con.requestMethod = "GET"
                for ((key, value) in requestHeaders) {
                    con.setRequestProperty(key, value)
                }
                val responseCode = con.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                    readBody(con.inputStream)
                } else { // 에러 발생
                    readBody(con.errorStream)
                }
            } catch (e: IOException) {
                throw java.lang.RuntimeException("API 요청과 응답 실패", e)
            } finally {
                con.disconnect()
            }
        }

        private fun connect(apiUrl: String): HttpURLConnection {
            return try {
                val url = URL(apiUrl)
                url.openConnection() as HttpURLConnection
            } catch (e: MalformedURLException) {
                throw java.lang.RuntimeException("API URL이 잘못되었습니다. : $apiUrl", e)
            } catch (e: IOException) {
                throw java.lang.RuntimeException("연결이 실패했습니다. : $apiUrl", e)
            }
        }

        private fun readBody(body: InputStream): String? {
            val streamReader = InputStreamReader(body)
            try {
                BufferedReader(streamReader).use { lineReader ->
                    val responseBody = StringBuilder()
                    var line: String? = ""
                    while (lineReader.readLine().also { line = it } != null) {
                        responseBody.append(line)
                    }
                    return responseBody.toString()
                }
            } catch (e: IOException) {
                throw java.lang.RuntimeException("API 응답을 읽는데 실패했습니다.", e)
            }
        }
    }
}
