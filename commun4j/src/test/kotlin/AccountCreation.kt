import com.squareup.moshi.Moshi
import io.golos.commun4j.BuildConfig
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberNameAdapter
import okhttp3.OkHttpClient
import okhttp3.Request

internal data class CommonUser(val userid: CyberName, val username: String, val active_key: String)

class AccountCreationTest {

    companion object {
        private const val eosCreateKey = BuildConfig.CREATE_KEY

        private val moshi = Moshi.Builder().add(CyberName::class.java, CyberNameAdapter()).build()
        private val okHttpClient = OkHttpClient.Builder().build();

        fun createNewAccount(forConfig: Commun4jConfig): Pair<CyberName, String> {
            val respo = okHttpClient.newCall(
                    Request.Builder()
                            .get()
                            .url("http://116.203.39.126:7777/generateCommunUser/5")
                            .build()
            ).execute()

            respo.body!!.string().let {
                val usr = moshi.adapter(CommonUser::class.java).fromJson(it)
                return Pair(usr!!.userid, usr.active_key)
            }
        }
    }
}