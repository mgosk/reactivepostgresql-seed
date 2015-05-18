package eb.core

import com.github.tminglei.slickpg._
import slick.driver.{PostgresDriver => SlickPostgresDriver}

trait PostgresDriver extends SlickPostgresDriver with PgDate2Support {
  override lazy val Implicit = new ImplicitsPlus {}
  override val api = MyAPI

  trait ImplicitsPlus extends Implicits with DateTimeImplicits

  object MyAPI extends API with DateTimeImplicits
}

object PostgresDriver extends PostgresDriver
