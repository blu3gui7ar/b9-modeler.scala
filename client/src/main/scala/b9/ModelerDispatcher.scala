package b9

import japgolly.scalajs.react.Callback
import monix.execution.{Ack, Cancelable}
import monix.execution.Ack.Continue
import monix.reactive.{Observable, Observer}
import monix.reactive.subjects.BehaviorSubject
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future


class Dispatcher [ModelerState](val initialModelerState: ModelerState){

  private val dispatcher: BehaviorSubject[ModelerState => ModelerState] =  BehaviorSubject.apply(identity)

  val stream: Observable[ModelerState] = dispatcher.scan(initialModelerState)((s, x) => x(s))


  def dispatch(f:ModelerState => ModelerState): Unit = dispatcher.onNext(f)

  def dispatchCB(f: ModelerState => ModelerState): Callback = Callback { dispatch(f) }

  def observer(f:ModelerState => Unit)= new Observer[ModelerState] {
    def onNext(s: ModelerState): Future[Ack] = {

      f(s)

      Continue
    }

    def onError(ex: Throwable): Unit = ex.printStackTrace()

    def onComplete(): Unit = println("Completed")
  }

  def subscribe(modeState: ModelerState => Unit, filter: ModelerState => Boolean): Cancelable =
    stream.filter(filter).subscribe(observer(modeState))

  def subscribe(modeState: ModelerState => Unit): Cancelable =
    stream.subscribe(observer(modeState))


  def subscribeOpt(modeState: ModelerState => Unit, filter: ModelerState => Boolean): Option[Cancelable] =
    Option(stream.filter(filter).subscribe(observer(modeState)))

  def subscribeOpt(modeState: ModelerState => Unit): Option[Cancelable] =
    Option(stream.subscribe(observer(modeState)))
}