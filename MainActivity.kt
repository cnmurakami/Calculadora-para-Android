package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                // A surface container using the "background" color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Calculator()
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Calculator() {
//-----------------------------------declarations
    val eraseColor: ButtonColors = ButtonDefaults.buttonColors(Color(255,57,46))
    val altColor: ButtonColors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
    val customHeight = LocalConfiguration.current.screenHeightDp
    val customWidth = LocalConfiguration.current.screenWidthDp
    var currentProblem: String by remember { //store the current user input formula
        mutableStateOf("")
    }
    var currentResult: String by remember { //store current result of the formula
        mutableStateOf("")
    }
    var showHistory: Boolean by remember { 
        mutableStateOf(false)
    }

    /*
    function to create buttons with default values which can be changed when called.
    Also handles the actions of the buttons, so when calling a button, minimally only the label is necessary as a parameter
    The label will be the displayed as the button text and will determine the action when pressed 
    */
    @Composable
    fun calcButton(
        label: String,
        modifier: Modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .aspectRatio(1f),
        shape: RoundedCornerShape = RoundedCornerShape(30.dp),
        colors: ButtonColors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        onClick: () -> Unit,
        customTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
        content: @Composable () -> Unit,
    ) {
        Button(shape = shape,
            modifier = modifier,
            colors = colors,
            onClick = {
                if (label == "C" || label == "AC"){
                    currentResult = ""
                    currentProblem = if (label == "C"){
                        ""
                    } else{
                        currentProblem.dropLast(1)
                    }
                }
                else if(label == "="){ //When the equals button is pressed, if the formula is valid, the current formula and results will be stored in history
                    currentResult = finalCalc(currentProblem)
                    if (currentResult != errorMsg){
                        problemHistory.add(currentProblem)
                        resultHistory.add(currentResult)
                    }
                }
                else {
                    currentResult = ""
                    currentProblem += label
                }
            }) {
            Text(text = label
                , style = customTextStyle
                , fontWeight = FontWeight.Bold
            )
            content()
        }
    }

//-----------------------------------main
    
//  -----------------------------------default layout
    if (!showHistory) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier //blank space
                    .fillMaxWidth()
                    .height(pxToDp(50f, customHeight).dp)
            ) {
            }
            Column(
                modifier = Modifier //input area
                    .fillMaxWidth()
                    .height(pxToDp(350f, customHeight).dp)
                    .padding(
                        horizontal = pxToDp(50f, customHeight).dp,
                        vertical = pxToDp(30f, customWidth, false).dp
                    )
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .clip(shape = RoundedCornerShape(9.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(4f)
                                .horizontalScroll(ScrollState(999999999)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = currentProblem
                                ,modifier = Modifier.padding(horizontal = 10.dp)
                                , style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(6f)
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(3f)
                                ) {
                                    Button(modifier = Modifier
                                        .padding(horizontal = 10.dp)

                                        , shape = RoundedCornerShape(5.dp)
                                        , onClick = { showHistory=true }) {
                                        Text(text = "Hist"
                                            , style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(8f)
                                        .horizontalScroll(ScrollState(999999999))
                                    , contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = currentResult,
                                        style = MaterialTheme.typography.headlineMedium
                                        ,fontWeight = FontWeight.ExtraBold
                                        ,modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
//          -----------------------------------buttons start
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pxToDp(1400f, customHeight).dp)
                    .padding(
                        horizontal = pxToDp(50f, customHeight).dp,
                        vertical = pxToDp(25f, customWidth, true).dp
                    )
            ) {
                Column(
                    modifier = Modifier //first row
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "C", colors = eraseColor, onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "(", colors = altColor, onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = ")", colors = altColor, onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "÷", colors = altColor, onClick = {}) {

                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier //second row
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "7", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "8", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "9", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "x", colors = altColor, onClick = {}) {

                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier //third row
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "4", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "5", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "6", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "-", colors = altColor, onClick = {}) {

                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier //fourth row
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "1", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "2", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "3", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "+", colors = altColor, onClick = {}) {

                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier //fifth row
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "0", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = ".", onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(label = "AC", colors = eraseColor, onClick = {}) {

                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            calcButton(
                                label = "=",
                                colors = ButtonDefaults.buttonColors(Color(255, 150, 79)),
                                onClick = {}) {

                            }
                        }
                    }
                }
            }
//          -----------------------------------buttons end
        }
    }
//  -----------------------------------history layout
    else{
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
        ){
            Row (modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ){
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 5.dp)
                    , contentAlignment = Alignment.CenterStart
                ){
                    Text(text = "History"
                        , modifier = Modifier.padding(horizontal = 20.dp)
                        , style = MaterialTheme.typography.headlineMedium
                        , fontWeight = FontWeight.Bold
                        )
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 5.dp)
                    , contentAlignment = Alignment.CenterEnd
                ){
                    Button(modifier = Modifier
                        .padding(horizontal = 20.dp)

                        , shape = RoundedCornerShape(5.dp)
                        , onClick = { showHistory=false }) {
                        Text(text = "Back"
                            , style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

            }
            Column (modifier = Modifier
                .fillMaxWidth()
                .weight(9f)
                .verticalScroll(ScrollState(999999999))) {
                for (i in 0..problemHistory.size-1){
                    Divider(modifier = Modifier.padding(horizontal = 30.dp), color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
                    Column (modifier = Modifier //input area
                        .fillMaxWidth()
                        .height(pxToDp(350f, customHeight).dp)
                        .padding(
                            horizontal = pxToDp(50f, customHeight).dp,
                            vertical = pxToDp(5f, customWidth, false).dp
                        )
                        .combinedClickable(interactionSource = MutableInteractionSource(),
                            indication = null,
                            onDoubleClick = {
                                currentProblem = problemHistory[i]
                                currentResult = ""
                                showHistory = false
                            } ){}
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(4f)
                                .horizontalScroll(ScrollState(999999999)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = problemHistory[i],
                                modifier = Modifier.padding(horizontal = 10.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(6f)
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(9f)
                                        .horizontalScroll(ScrollState(999999999))
                                    , contentAlignment = Alignment.CenterEnd

                                ) {
                                    Text(
                                        text = resultHistory[i],
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}






@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculatorTheme {
        Calculator()
    }
}


//-----------------------------------Calculations functions
//public variables, may be used either by main, calculations functions or both
val validOperators= listOf(
    '+', '-', 'x', '÷'
)

val notNumbers = listOf(
    '+', '-', 'x', '÷', '(', ')'
)

var problemHistory = mutableListOf<String>()

var resultHistory = mutableListOf<String>()

val errorMsg = "Invalid Formula"

//Converts layout size in pixel do dp
fun pxToDp(sizeToConvert:Float,dimension:Int,isHeight:Boolean=true): Float {
    val baseSize: Float = if (isHeight) {
        1900f
    }
    else{
        900f
    }
    return dimension.toFloat() * sizeToConvert / baseSize
}

/*
Gets the current formula and returns a list containing only the numbers (and points) as a list of strings, which will be used later
Should NOT be called on its own, it's included in the finalCalc function
*/
fun getNumbers(currentProblem:String):MutableList<String>{
    var tempNumber = ""
    val currentNumbers = mutableListOf<String>()
    for (i in 0..currentProblem.length-1){
        if (currentProblem[i] !in notNumbers || (currentProblem[i] == '-' && currentProblem[i-1] in notNumbers)){
            tempNumber+=currentProblem[i]
        }
        else{
            currentNumbers.add(tempNumber)
            tempNumber = ""
        }
    }
    currentNumbers.add(tempNumber)
    return currentNumbers
}

/*
Gets the current formula and returns a list containing only the operations, which will be used later
Should NOT be called on its own, it's included in the finalCalc function
*/
fun getOperations(currentProblem:String):MutableList<Char>{
    val currentOperations = mutableListOf<Char>()
    for (i in 0..currentProblem.length-1){
        if (currentProblem[i] in validOperators){
            if (!(currentProblem[i]=='-' && currentProblem[i-1] in notNumbers)){
                currentOperations.add(currentProblem[i])
            }
        }
    }
    return currentOperations
}

/*
Gets the number and operations list and performs the plus and minus operations, updating both lists
The operation performed is removed from the operations lists
For the numbers, it gets the pair involved in the operation (should be the same as the operation position and the next one). Updates the value of the first number with the result and removes the second from the number list
For accuracy and dealing with big numbers, double is used for the numbers
Should NOT be called on its own, it's included in the finalCalc function
*/
fun sumOperations(currentNumbers:MutableList<String>,currentOperations:MutableList<Char>):Double{
    var totalSum = currentNumbers[0].toDouble()
    currentNumbers.removeAt(0)
    var i=0
    while (i < currentNumbers.size){
        if (currentOperations[i] == '+') {
            totalSum += currentNumbers[i].toDouble()
            currentNumbers.removeAt(i)
            currentOperations.removeAt(i)
        }
        else if (currentOperations[i] == '-') {
            totalSum -= currentNumbers[i].toDouble()
            currentNumbers.removeAt(i)
            currentOperations.removeAt(i)
        }
        else{
            i++
        }
    }
    return totalSum
}

/*
Gets the number and operations list and performs the multiply and division operations, updating both lists
The operation performed is removed from the operations lists
For the numbers, it gets the pair involved in the operation (should be the same as the operation position and the next one). Updates the value of the first number with the result and removes the second from the number list
Also checks for errors if the result is infinite, which also will be the case of zero division when using doubles
Should NOT be called on its own, it's included in the finalCalc function
*/
fun multiplyOperations(currentNumbers:MutableList<String>,currentOperations:MutableList<Char>){
    var tempMultiply: Double
    var i=0
    while (i < currentOperations.size){
        if (currentOperations[i] == 'x') {
            tempMultiply = currentNumbers[i].toDouble() * currentNumbers[i+1].toDouble()
            if (!tempMultiply.isFinite()){
                throw Exception("Error")
            }
            currentNumbers.removeAt(i+1)
            currentNumbers.removeAt(i)
            currentOperations.removeAt(i)
            currentNumbers.add(i, tempMultiply.toString())
        }
        else if (currentOperations[i] == '÷') {
            tempMultiply = currentNumbers[i].toDouble() / currentNumbers[i+1].toDouble()
            if (!tempMultiply.isFinite()){
                throw Exception("Error")
            }
            currentNumbers.removeAt(i+1)
            currentNumbers.removeAt(i)
            currentOperations.removeAt(i)
            currentNumbers.add(i, tempMultiply.toString())
        }
        else {
            i++
        }
    }
    return
}

/*
Every minus in the formula is treated accordingly to account for negative numbers
If the first digit of the formula inputted by the user is a minus, it's assumed the the user meant to use a negative number
To deal with this, a 0 is added before. For instance, if the current formula is "-1+1", it becomes "0-1+1"
The function also checks if there are any double minus in the formula, whether it's user inputted or consequence of a iteration. If there are a double minus, they are replaced by a single plus
Should NOT be called on its own, it's included in the finalCalc function
*/
fun normalizeMinus(currentProblem: String):String{
    var currentProblem = currentProblem

    if (currentProblem[0]=='-') {
        currentProblem = currentProblem.substring(0, 0) + '0' + currentProblem.substring(0)
    }
    var i = 1
    while (i in 1..currentProblem.length-1) {
        if (currentProblem[i] == '-' && currentProblem[i - 1] == '-') {
            currentProblem = currentProblem.substring(0, i-1) + '+' + currentProblem.substring(i + 1)
            i-=1
        }
        i++
    }
    return currentProblem
}

/*
Function to check and sort parenthesis in the formula
It accounts multiple openings to check where the first is closed, to resolve a parenthesis pair inside another
When a formula is found within opened and closed parenthesis, the formula will be calculated and replaced in the main formula, iterating for each instance of a parenthesis pair
Should NOT be called on its own, it's included in the finalCalc function
 */
fun sortParenthesis(currentProblem: String):String{
    var currentProblem = currentProblem
    var openParenthesis=false
    var closedParenthesis=true
    var temp = ""
    var i=0
    var skip=0
    while (closedParenthesis){
        closedParenthesis=false
        while (i < currentProblem.length){
            if (currentProblem[i] == '(' || currentProblem[i] == ')'){
                if (currentProblem[i]== '('){
                    skip+=1
                    if (openParenthesis){
                        temp+=currentProblem[i]
                    }
                    else{
                        openParenthesis=true
                    }
                }
                else {
                    skip -= 1
                    if (skip == 0) {
                        openParenthesis = false
                        val tempResult = finalCalc(temp)
                        temp=""
                        for (j in 0..tempResult.length-1) {
                            val currentProblemSb = StringBuilder(currentProblem)
                            currentProblemSb.insert(i + 1 + j, tempResult[j])
                            currentProblem = currentProblemSb.toString()
                        }
                    } else {
                        temp+=currentProblem[i]
                    }
                }
                currentProblem = currentProblem.substring(0,i) + currentProblem.substring(i+1)
                i-=1
            }
            else if (openParenthesis){
                temp+=currentProblem[i]
                currentProblem = currentProblem.substring(0,i) + currentProblem.substring(i+1)
                i-=1
            }
            i+=1
        }
    }
    return currentProblem
}

/*
Rounds the decimals value if above 6 positions.
When dealing with doubles, for accuracy, a scientific connotation may be the result.
If the result is a scientific connotation, it will not be treated and returned in full
Should NOT be called on its own, it's included in the finalCalc function
*/
fun roundResult(currentResult:Double):String{
    var currentResultStr = currentResult.toString()
    if (currentResult>=100000000){
        return currentResult.toString()
    }
    else if (currentResultStr[currentResultStr.length-1] == '0' && currentResultStr[currentResultStr.length-2] =='.'){
        currentResultStr = currentResultStr.substring(0,currentResultStr.length-2)
        return currentResultStr
    }
    else{
        val tempResult = currentResultStr.split(".")
        if (tempResult[1].length>=6) {
            return String.format("%.6f", currentResult)
        }
        else
            return currentResult.toString()
    }
}

/*
The final calculation function
This is the only function that should be called when calculating a formula
Will deal with all the necessary functions and their order to return the correct answer, accounting also for errors
When a formula is valid, will return the result in a string, fully treated by the previous functions
If any error occurs, either in this function or any of the previous, will return the default error message stored in the public variable for easy editing if necessary
*/
fun finalCalc(currentProblem:String):String{
    try {
        var currentProblem = currentProblem
        currentProblem = sortParenthesis(currentProblem)
        currentProblem = normalizeMinus(currentProblem)
        val currentOperations = getOperations(currentProblem)
        val currentNumbers = getNumbers(currentProblem)
        multiplyOperations(currentNumbers, currentOperations)
        val currentResult = sumOperations(currentNumbers, currentOperations)
        return roundResult(currentResult)
    }
    catch (e:Exception){
        return errorMsg
    }
}