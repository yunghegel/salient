import kotlinx.coroutines.flow.MutableSharedFlow
import org.yunghegel.gdx.effect.EventBus
import org.yunghegel.gdx.effect.event.Event
import org.yunghegel.gdx.effect.event.EventPayload
import kotlin.test.Test

class EventTests {
    val flow = MutableSharedFlow<Event>()

    init {

    }

    @Test fun testEvent() {
        flow.tryEmit(TestEvent)
    }

    @Test fun testSubscribe() {
        EventBus.initiate()
        EventBus.subscribe(TestEvent::class, { p -> println("Test") }, listener = { e-> println("Success"); EventPayload(e.args) } )
        EventBus.post(TestEvent)
    }

}

object TestEvent : Event()
object TestEvent2 : Event()
