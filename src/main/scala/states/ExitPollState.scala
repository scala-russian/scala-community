package states

import eu.timepit.refined.boolean._
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.string._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import shapeless._
import states.ExitPollState._

case class ExitPollState(
    title: NeString,
    description: String,
    generalAppraisal: Appraisal,
    talks: Section,
    organisation: Section,
    community: Section)
    extends SocietyState

object ExitPollState {
  type VoteRange   = Int Refined Interval.Closed[W.`1`.T, W.`5`.T]
  type NeVector[T] = Vector[T] Refined NonEmpty
  type NeString = String Refined NonEmpty

  sealed trait Question {def question: NeString}

  case class Answer[T](label: NeString Refined NonEmpty, value: Option[T])

  case class Appraisal(
      question: NeString,
      lowValue: String,
      highValue: String,
      answer: Answer[VoteRange])
      extends Question

  case class FreeAnswer(question: NeString, answer: Answer[String]) extends Question

  case class SingleAnswer(
      question: NeString,
      options: NeVector[Answer[String]],
      withOtherAnswer: Boolean = false)
      extends Question

  case class MultiAnswer(
      question: NeString,
      options: NeVector[Answer[String]],
      withOtherAnswer: Boolean = false)
      extends Question

  case class Section(title: String, questions: NeVector[Question])

}
