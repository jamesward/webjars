package utils

import java.util.concurrent.TimeUnit

import play.api.test._

import scala.concurrent.ExecutionContext
import scala.util.Try

class BowerSpec extends PlaySpecification {

  val ws = StandaloneWS.apply()
  val bower = Bower(ExecutionContext.global, ws.client)

  "jquery info" should {
    "work with a correct version" in {
      await(bower.info("jquery", "1.11.1")).name must equalTo("jquery")
    }
    "fail with an invalid version" in {
      await(bower.info("jquery", "0.0.0")) must throwA[Exception]
    }
    "have a license" in {
      await(bower.info("jquery", "1.11.1")).licenses must contain("MIT")
    }
  }
  "bootstrap" should {
    "have a license" in {
      await(bower.info("bootstrap", "3.3.2")).licenses must contain("MIT")
    }
    "have a dependency on jquery" in {
      await(bower.info("bootstrap", "3.3.2")).dependencies must contain("jquery" -> ">= 1.9.1")
    }
  }
  "dc.js" should {
    "have the corrected source url" in {
      await(bower.info("dc.js", "1.7.3")).sourceUrl must beEqualTo("git://github.com/dc-js/dc.js.git")
    }
  }
  "sjcl" should {
    "download" in {
      await(bower.zip("sjcl", "1.0.2"), 1, TimeUnit.MINUTES).available() must beGreaterThan(0)
    }
  }
  "gitHubLicenseDetect" should {
    "detect the license" in {
      await(bower.gitHubLicenseDetect(Try("twbs/bootstrap"))) must beEqualTo("MIT")
    }
    "detect another license" in {
      await(bower.gitHubLicenseDetect(Try("angular/angular"))) must beEqualTo("Apache-2.0")
    }
  }

  step(ws.close())

}