package com.example.gauss_calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView expression_view, result_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init text views
        expression_view = findViewById(R.id.expression_view);
        result_view = findViewById(R.id.result_view);
    }

    // Function to add numbers to expression
    public void setNumber(String number) {
        String expression = expression_view.getText().toString();
        char last_char = expression.charAt(expression.length() - 1);

        // If display value is 0, keep displaying 0
        if (expression.length() > 1) {
            char second_last_char = expression.charAt(expression.length() - 2);
            if (last_char == '0' && isOperation(second_last_char) && number.equals("0")) {
                expression_view.setText(expression);
            } else if (last_char == '0' && isOperation(second_last_char) && !number.equals("0")) {
                expression_view.setText(expression.substring(0, expression.length() - 1));
                expression_view.append(number);
            } else {
                expression_view.append(number);
            }
        } else {
            if (expression.equals("0")) {
                expression_view.setText(number);
            } else {
                expression_view.append(number);
            }
        }
    }

    // Function to add symbols to expression
    public void setSymbol(String symbol) {
        String expression = expression_view.getText().toString();
        char last_char = expression.charAt(expression.length() - 1);

        int aux = 0;
        boolean setDot = true;

        if (isOperation(last_char)) aux = 1;

        for (int i = 0; i < expression.length() - aux; i++) {
            if (isOperation(expression.charAt(i))) {
                setDot = true;
            } else if (expression.charAt(i) == '.'){
                setDot = false;
            }
        }

        // If theres a dot already, then skip the incoming dot
        if (!setDot && symbol.equals(".")) return;

        // Delete symbol if symbol is at end of expression
        if (isOperation(last_char) || last_char == '.') {
            expression_view.setText(expression.substring(0, expression.length() - 1));
        }

        expression_view.append(symbol);
    }

    // Function to evaluate the expression
    public void evaluateExpression(String expression) {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Scriptable scope = context.initStandardObjects();

        char last_char = expression.charAt(expression.length() - 1);

        // Clean expression
        if (isOperation(last_char) || last_char == '.') {
            expression = expression.substring(0, expression.length() - 1);
        }

        // Format expression with valid symbols
        expression = expression.replace('×', '*');
        expression = expression.replace('÷', '/');
        expression = replaceExponentiation(expression);

        try {
            // Evaluate expression
            Object result = context.evaluateString(scope, expression, "<cmd>", 1, null);
            String resultString = result.toString();

            // Clear .0
            if (resultString.charAt(resultString.length() - 1) == '0' && resultString.charAt(resultString.length() - 2) == '.') {
                resultString = resultString.substring(0, resultString.length() - 2);
            }
            // Display result
            String finalResult = "= " + resultString;
            result_view.setTextColor(getColor(R.color.gray));
            result_view.setText(finalResult);
        } catch (Exception illegal_e) {
            // Display error
            result_view.setTextColor(getColor(R.color.danger));
            result_view.setText("ERROR");
        }

    }

    /*
    =============================================
    Change exponentiation format
    =============================================
    */

    public String replaceExponentiation(String expression) {
        ArrayList<Integer> exponentiationIndex = new ArrayList<>();
        String initialExpression = expression;

        // Identify exponentiation symbols in expression
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '^') exponentiationIndex.add(i);
        }

        // Change every exponentiation expression
        for (Integer i : exponentiationIndex) {
            expression = changeExponentiationExpression(i, initialExpression, expression);
        }

        return expression;
    }

    // Change exponentiation format
    public String changeExponentiationExpression(Integer index, String initialExpression, String expression) {
        StringBuilder leftNum = new StringBuilder();
        StringBuilder rightNum = new StringBuilder();

        for (int i = index + 1; i < initialExpression.length(); i++) {
            if (isNumeric(initialExpression.charAt(i))) {
                rightNum.append(initialExpression.charAt(i));
            } else {
                break;
            }
        }

        for (int i = index - 1; i >= 0; i--) {
            if (isNumeric(initialExpression.charAt(i))) {
                leftNum.append(initialExpression.charAt(i));
            } else {
                break;
            }
        }
        leftNum.reverse();

        String original = leftNum + "^" + rightNum;
        String changed = "Math.pow(" + leftNum + "," + rightNum + ")";

        return expression.replace(original, changed);
    }

    // Validate if it's number or not
    public boolean isNumeric(char n) {
        return (n <= '9' && n >= '0') || n == '.';
    }

    // Validate if it's operation or not
    public boolean isOperation(char o) {
        return o == '+' || o == '-' || o == '×' || o == '÷' || o == '^';
    }

    /*
    =============================================
    On click listeners
    =============================================
    */
    public void clearAllOnClick(View view) {
        expression_view.setText("0");
        result_view.setText("= 0");
        result_view.setTextColor(getColor(R.color.gray));
    }

    public void deleteOnClick(View view) {
        String expression = expression_view.getText().toString();

        // If expression is null, then display 0
        if (expression.length() > 1) {
            expression_view.setText(expression.substring(0, expression.length() - 1));
        } else {
            expression_view.setText("0");
        }

        evaluateExpression(expression_view.getText().toString());
    }

    public void exponentiationOnClick(View view) {
        setSymbol("^");
        evaluateExpression(expression_view.getText().toString());
    }

    public void divisionOnClick(View view) {
        setSymbol("÷");
        evaluateExpression(expression_view.getText().toString());
    }

    public void sevenOnClick(View view) {
        setNumber("7");
        evaluateExpression(expression_view.getText().toString());
    }

    public void eightOnClick(View view) {
        setNumber("8");
        evaluateExpression(expression_view.getText().toString());
    }

    public void nineOnClick(View view) {
        setNumber("9");
        evaluateExpression(expression_view.getText().toString());
    }

    public void multiplicationOnClick(View view) {
        setSymbol("×");
        evaluateExpression(expression_view.getText().toString());
    }

    public void fourOnClick(View view) {
        setNumber("4");
        evaluateExpression(expression_view.getText().toString());
    }

    public void fiveOnClick(View view) {
        setNumber("5");
        evaluateExpression(expression_view.getText().toString());
    }

    public void sixOnClick(View view) {
        setNumber("6");
        evaluateExpression(expression_view.getText().toString());
    }

    public void subtractionOnClick(View view) {
        setSymbol("-");
        evaluateExpression(expression_view.getText().toString());
    }

    public void oneOnClick(View view) {
        setNumber("1");
        evaluateExpression(expression_view.getText().toString());
    }

    public void twoOnClick(View view) {
        setNumber("2");
        evaluateExpression(expression_view.getText().toString());
    }

    public void threeOnClick(View view) {
        setNumber("3");
        evaluateExpression(expression_view.getText().toString());
    }

    public void additionOnClick(View view) {
        setSymbol("+");
        evaluateExpression(expression_view.getText().toString());
    }

    public void zeroOnClick(View view) {
        setNumber("0");
        evaluateExpression(expression_view.getText().toString());
    }

    public void dotOnClick(View view) {
        setSymbol(".");
        evaluateExpression(expression_view.getText().toString());
    }

    public void resultOnClick(View view) {
        String resultString = result_view.getText().toString();
        String resultValue = resultString.substring(2);

        expression_view.setText(resultValue);
        evaluateExpression(expression_view.getText().toString());
    }

}