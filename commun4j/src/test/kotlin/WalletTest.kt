import io.golos.commun4j.model.FeedType
import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryTransferType
import io.golos.commun4j.services.model.WalletQuantity
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import org.junit.Test

class WalletTest {
    val client = getClient(authInServices = true)
    @Test
    fun getBalanceTest() {
        client.getBalance(client.keyStorage.getActiveAccount()).getOrThrow()
        client.getPosts(type = FeedType.TOP_LIKES).getOrThrow().forEach {
            client.getBalance(it.author.userId).getOrThrow()
        }
    }

    @Test
    fun getTransactionTest() {
        client.getTransferHistory(client.keyStorage.getActiveAccount(), limit = 1000).getOrThrow()
        client.getTransferHistory(client.keyStorage.getActiveAccount(), TransferHistoryDirection.RECEIVE).getOrThrow()
        client.getTransferHistory(client.keyStorage.getActiveAccount(), TransferHistoryDirection.RECEIVE, TransferHistoryTransferType.NONE).getOrThrow()
    }

    @Test
    fun getBuyPriceTest() {
        client.getBuyPrice(CyberSymbolCode("MEME"), WalletQuantity("10 CMN")).getOrThrow()
    }

    @Test
    fun getSellPriceTest() {
        client.getSellPrice(WalletQuantity("10 MEME")).getOrThrow()
    }

}