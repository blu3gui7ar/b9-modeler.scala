package b9

import japgolly.scalajs.react.Callback
import monix.execution.{Ack, Cancelable}
import monix.execution.Ack.Continue
import monix.reactive.{Observable, Observer}
import monix.reactive.subjects.BehaviorSubject
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future


class Dispatcher [S](val initialModelerState: S){

  private val dispatcher: BehaviorSubject[S => S] =  BehaviorSubject.apply(identity)

  val stream: Observable[S] = dispatcher.scan(initialModelerState)((s, x) => x(s))


  def dispatch(f:S => S): Unit = dispatcher.onNext(f)

  def dispatchCB(f: S => S): Callback = Callback { dispatch(f) }

  def observer(f:S => Unit)= new Observer[S] {
    def onNext(s: S): Future[Ack] = {

      f(s)

      Continue
    }

    def onError(ex: Throwable): Unit = ex.printStackTrace()

    def onComplete(): Unit = println("Completed")
  }

  def subscribe(modeState: S => Unit, filter: S => Boolean): Cancelable =
    stream.filter(filter).subscribe(observer(modeState))

  def subscribe(modeState: S => Unit): Cancelable =
    stream.subscribe(observer(modeState))


  def subscribeOpt(modeState: S => Unit, filter: S => Boolean): Option[Cancelable] =
    Option(stream.filter(filter).subscribe(observer(modeState)))

  def subscribeOpt(modeState: S => Unit): Option[Cancelable] =
    Option(stream.subscribe(observer(modeState)))
}