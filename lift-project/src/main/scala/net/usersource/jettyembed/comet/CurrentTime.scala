
package net.usersource.jettyembed.comet

import net.liftweb.http.js.JsCmds.SetHtml
import scala.xml.NodeSeq
import net.liftweb.common.{Full, Box}
import net.liftweb.http.{CometActor, LiftSession}
import net.liftweb.util.ActorPing
import net.liftweb.util.Helpers._
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormatterBuilder

case object Tick

class CurrentTime(initSession: LiftSession,
                  initType: Box[String],
                  initName: Box[String],
                  initDefaultXml: NodeSeq,
                  initAttributes: Map[String, String]) extends CometActor {


  override def defaultPrefix = Full("comet")

  setPingIn

  private lazy val spanId = uniqueId + "_current_time"



  def setPingIn = ActorPing.schedule(this, Tick, 1 seconds)

  def render = {
    bind("currentTime" -> span)
  }

  def getCurrentTime = {

    val dt = new DateTime
    val dtLondon = dt.toDateTime(DateTimeZone.forID("Europe/London"));
    val dtStockholm = dt.toDateTime(DateTimeZone.forID("Europe/Stockholm"));
    val fmt = new DateTimeFormatterBuilder()
                  .appendHourOfDay(2)
                  .appendLiteral(':')
                  .appendMinuteOfHour(2)
                  .appendLiteral(':')
                  .appendSecondOfMinute(2)
                  .appendLiteral(" - ")
                  .appendDayOfMonth(2)
                  .appendLiteral(' ')
                  .appendMonthOfYearText
                  .appendLiteral(' ')
                  .appendYear(4,4)
                  .toFormatter;

    <span id="current_time">
      <span id="current_time_london">London {dtLondon.toString(fmt)}<br/></span>
      <span id="current_time_stockholm">Stockholm {dtStockholm.toString(fmt)}<br/></span>
    </span>
  }

  def span = (<span id={spanId}>{getCurrentTime}</span>)

  override def lowPriority = {
    case Tick =>
      partialUpdate(SetHtml(spanId, getCurrentTime))
      setPingIn
  }


  initCometActor(initSession, initType, initName, initDefaultXml, initAttributes)
}
