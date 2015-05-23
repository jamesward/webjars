package utils

import java.util.Calendar

object VersionOrdering extends Ordering[String] {

  def unmalform(s: String): String = {
    val fixBeta = s.replaceAllLiterally("beta", ".beta.")
    val fixRc = fixBeta.replaceAllLiterally("rc", ".rc.")
    val justDots = fixRc.replaceAllLiterally("-", ".").replaceAllLiterally("..", ".")
    val betterDate = if (justDots.matches("(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)")) {
      val parts = justDots.split('.').map(_.toInt)
      val time = Calendar.getInstance()
      time.set(parts(2), parts(0), parts(1))
      time.getTimeInMillis.toString
    } else {
      justDots
    }
    betterDate
  }

  def compare(a: String, b: String) = {

    def betaRc(s: String): String = {
      s match {
        case "beta" => "-2"
        case "rc" => "-1"
        case _ => s
      }
    }

    val aParts = unmalform(a).split('.').map(betaRc)
    val bParts = unmalform(b).split('.').map(betaRc)

    // figure out the longest one and pad each with a string 0 until the sizes match
    val longest = aParts.length max bParts.length

    val paddedAParts = aParts.padTo(longest, "0")
    val paddedBParts = bParts.padTo(longest, "0")

    // combine the two arrays into one with tuples
    val abParts = paddedAParts zip paddedBParts

    // compare all the parts as ints
    // todo: could be optimized
    val abCompared = abParts.map { case (aV, bV) =>
      try {
        aV.toLong compare bV.toLong
      }
      catch {
        // if we can't compare because it's not an int then just set this one to 1
        case e: NumberFormatException => 1
      }
    }

    // we only care about the first non-zero compare
    // if we get to the end and everything was equal then return 0
    abCompared.dropWhile(_ == 0).headOption.getOrElse(0)
  }

}
