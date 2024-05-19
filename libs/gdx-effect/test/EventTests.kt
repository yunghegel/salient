import kotlinx.coroutines.flow.MutableSharedFlow
import org.yunghegel.gdx.effect.EventBus
import org.yunghegel.gdx.effect.event.Event
import kotlin.test.Test

class EventTests {
    val flow = MutableSharedFlow<Event>()

    init {
        EventBus.initiate()
    }

    @Test fun testEvent() {
        flow.tryEmit(TestEvent)
    }

    @Test fun testSubscribe() {
        EventBus.subscribe(TestEvent::class, { println(it) })
        EventBus.post(TestEvent)
    }

}

object TestEvent : Event()
object TestEvent2 : Event()
