package converter

import kotlin.system.exitProcess

fun main() {

    while (true) {
        print("Enter what you want to convert (or exit): ")
        val string = readln().lowercase()
        val formatString = string.replace("degree ", "degree").replace("degrees ", "degrees")
        val input = formatString.split(" ")
        if (input.size == 4) {
            val (value, inputMeasure, _, destinMeasure) = input
            try {
                Units().theSameType(value.toDouble(), inputMeasure, destinMeasure)
            } catch (e: Exception) {
                println("Parse error")
                println()
            }
        } else if (input[0] == "exit") {
            exitProcess(0)
        } else {
            println("Parse error")
            println()
        }
    }
}

class Units {
    private val lengthUnits = mapOf(
        "basic" to listOf("m", "meter", "meters", 1.0), //basic == meters
        "kilometers" to listOf("km", "kilometer", "kilometers", 1000.0),
        "centimeters" to listOf("cm", "centimeter", "centimeters", 0.01),
        "millimeters" to listOf("mm", "millimeter", "millimeters", 0.001),
        "miles" to listOf("mi", "mile", "miles", 1609.35),
        "yards" to listOf("yd", "yard", "yards", 0.9144),
        "feet" to listOf("ft", "foot", "feet", 0.3048),
        "inches" to listOf("in", "inch", "inches", 0.0254)
    )

    private val weightUnits = mapOf(
        "basic" to listOf("g", "gram", "grams", 1.0), //basic == grams
        "kilograms" to listOf("kg", "kilogram", "kilograms", 1000.0),
        "milligrams" to listOf("mg", "milligram", "milligrams", 0.001),
        "pounds" to listOf("lb", "pound", "pounds", 453.592),
        "ounces" to listOf("oz", "ounce", "ounces", 28.3495)
    )

    private val temperatureUnits = mapOf(
        "basic" to listOf("k", "kelvin", "kelvins", "1.0"), //basic == grams
        "celsius" to listOf("c", "degree Celsius", "degrees Celsius", "degreecelsius","degreescelsius", "celsius", "dc"),
        "fahrenheit" to listOf("f", "degree Fahrenheit", "degrees Fahrenheit", "degreefahrenheit", "degreesfahrenheit", "fahrenheit", "df")

    )


    private val unitsList = listOf(lengthUnits, weightUnits, temperatureUnits)

    private fun convert(value: Double, inputMeasure: String, destinMeasure: String, unitsType: Map<out String?, List<Any?>>) {
        for (i in unitsType) {

            if (unitsType[i.key]!!.contains(inputMeasure)) {
                val choseUnits = unitsType[i.key]
                val convertValueBasic = value * (choseUnits!![3] as Double) // to basic units

                for (d in unitsType) {
                    if (unitsType[d.key]!!.contains(destinMeasure)) {
                        val destinUnits = unitsType[d.key]
                        val convertValueDestin = convertValueBasic / (destinUnits!![3] as Double) // to destin units
                        val measure = if (value == 1.0) choseUnits[1] as String else {
                            choseUnits[2] as String
                        }
                        val convertMeasure = if (convertValueDestin == 1.0) destinUnits[1] as String else {
                            destinUnits[2] as String
                        }

                        printConvert(value, measure, convertValueDestin, convertMeasure)
                        return
                        }
                    }
                }
        }
    }

    private fun temperatureConvert(value: Double, inputMeasure: String, destinMeasure: String, unitsType: Map<out String?, List<Any?>>){
        for (i in unitsType) {
                if (unitsType[i.key]!!.contains(inputMeasure)) {
                    val choseUnits = unitsType[i.key]
                    val convertValueBasic = when(i.key) {
                        "fahrenheit" -> { (value + 459.67) * (5.0 / 9.0) }
                        "celsius" -> { value + 273.15 }
                        else -> value
                    }

                    for (d in unitsType) {
                        if (unitsType[d.key]!!.contains(destinMeasure)) {
                            val destinUnits = unitsType[d.key]
                            val convertValueDestin = when(d.key) {
                                "fahrenheit" -> { convertValueBasic * (9.0 / 5.0) - 459.67 }
                                "celsius" -> { convertValueBasic - 273.15 }
                                else -> convertValueBasic
                            }

                            val measure = if (value == 1.0) choseUnits!![1] as String
                            else { choseUnits!![2] as String }
                            val convertMeasure = if (convertValueDestin == 1.0) destinUnits!![1] as String
                            else { destinUnits!![2] as String }
                            printConvert(value, measure, convertValueDestin, convertMeasure)
                            return
                        }
                    }
                }
            }
    }

    private fun printConvert(value: Double, inputMeasure: String, convertValue: Double, convertMeasure: String) {
        println("$value $inputMeasure is $convertValue $convertMeasure")
        println()
    }

    fun theSameType(value: Double, inputMeasure: String, destinMeasure: String) {
        var notSameInputMeasure = "???"
        var notSameDestinMeasure = "???"
        for (i in unitsList) {
            if (i.any { it.value.contains(inputMeasure) } && i.any { it.value.contains(destinMeasure) }) {
                if (value < 0) {
                    when(i){
                        lengthUnits -> {
                            println("Length shouldn't be negative")
                            println()
                            return
                        }
                        weightUnits-> {
                            println("Weight shouldn't be negative")
                            println()
                            return
                        }
                        temperatureUnits -> {}
                    }
                }
                if (i == temperatureUnits) {
                    temperatureConvert(value, inputMeasure, destinMeasure, i)
                    return
                }
                convert(value,inputMeasure, destinMeasure, i)
                return
            } else if (i.any { it.value.contains(inputMeasure) } || i.any { it.value.contains(destinMeasure) }) {
                if (i.any { it.value.contains(inputMeasure) }) {
                    val key = i.entries.firstOrNull { entry -> entry.value.any { it == inputMeasure } }?.key.toString()
                    val choseUnits = i[key]
                    notSameInputMeasure =  choseUnits!![2] as String
                }
                if (i.any { it.value.contains(destinMeasure) }) {
                    val key = i.entries.firstOrNull { entry -> entry.value.any { it == destinMeasure } }?.key.toString()
                    val choseUnits = i[key]
                    notSameDestinMeasure = choseUnits!![2] as String
                }
            }
        }
        println("Conversion from $notSameInputMeasure to $notSameDestinMeasure is impossible")
        println()
    }
}
