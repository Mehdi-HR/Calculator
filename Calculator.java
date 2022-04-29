import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import static java.lang.Character.isDigit;

public class Calculator extends JFrame implements ActionListener {

    //texte affiche
    private String text = "";
    //ce qui est affiche est un resultat
    private boolean isResult = false;

    //liste
    private String[] list;
    //postfix
    private String[] postfix;

    private String[] textToList(){
        ArrayList<String> list = new ArrayList<String>();
        String current = "";
        char[] textArray = this.text.toCharArray();
        for (int i = 0; i < textArray.length; i++) {
            char c = textArray[i];
            if (isDigit(c) || c == '.') {
                current += c;
                if( i == textArray.length - 1)
                    list.add(current);
            }
            else {
                if ( !current.isEmpty() )
                    list.add(current);
                list.add("" + c);
                current = "";
            }
        }
        return list.toArray(new String[0]);
    }


    //analyse syntaxique , implementation directe de l'automate
    private void initLists(){
        list = null;
        postfix = null;
        index = 0;
    }
    private int index;
    private void advance(){
        index++;
        //System.out.println(index);
    }
    private boolean isValidExpression(){
        list = textToList();
        for (int i = 0; i < list.length; i++)
            System.out.print(list[i]);
        System.out.println();
        return E();
    }


    private boolean E(){
        //System.out.println("E");
        if (index >= list.length ) return false;
        if ( T() )
            return E_();
        else return false;
    }

    private boolean E_(){
        //System.out.println("E_");
        if ( index == list.length )
            return true;
        if ( list[index].equals("+")  || list[index].equals("-") ){
            advance();
            if ( T() )
                return E_();
            else return false;
        }
        return true;
    }

    private boolean T(){
        //System.out.println("T");
        if (index >= list.length ) return false;
        if (F())
            return T_();
        else return false;
    }

    private boolean T_(){
        //System.out.println("T_");
        if ( index == list.length )
            return true;
        if ( list[index].equals("×")  || list[index].equals("÷") ){
             advance();
            if ( F() )
                return T_();
            else return false;
        }
        return true;
    }

    private boolean F(){
        //System.out.println("F");
        if (index >= list.length ) return false;
        if ( list[index].equals("(") ){
            advance();
            if ( E() ) {
                if (list[index].equals(")")) {
                    advance();
                    return true;
                } else return false;
            }else return false;
        }
        if ( isNumeric(list[index]) ) {
            advance();
            //return index == list.length;
            return true;
        }
        return false;
    }

    //verifier si c'est un nombre
    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    //convertir en notation postfixe
    static int Prec(String opt)
    {
        switch (opt)
        {
            case "+":
            case "-":
                return 1;

            case "*":
            case "/":
                return 2;

        }
        return -1;
    }

    // The main method that converts
    // given infix expression
    // to postfix expression.


    private String[] listToPostFix() {

        if ( isValidExpression() ){
            // initializing empty String for result
            ArrayList<String> result = new ArrayList<>();

            // initializing empty stack
            Stack<String> stack = new Stack<>();

            for (int i = 0; i<list.length; ++i)
            {
                String current = list[i];

                // If the scanned character is an
                // operand, add it to output.
                if (isNumeric(current))
                    result.add(current);

                    // If the scanned character is an '(',
                    // push it to the stack.
                else if (current.equals("("))
                    stack.push(current);

                    //  If the scanned character is an ')',
                    // pop and output from the stack
                    // until an '(' is encountered.
                else if (current.equals(")"))
                {
                    //System.out.println("stack :" + stack);
                    while (!stack.isEmpty() &&
                            !stack.peek().equals("("))
                        result.add(stack.pop());
                    if (!stack.isEmpty())
                        stack.pop();
                }
                else // an operator is encountered
                {
                    while (!stack.isEmpty() && Prec(current)
                            <= Prec(stack.peek())){

                        result.add(stack.pop());
                    }
                    stack.push(current);
                }

            }

            // pop all the operators from the stack
            while (!stack.isEmpty()){
                if(stack.peek().equals("("))
                    return null;
                result.add(stack.pop());
            }
            while (result.remove("(")) ;
            return result.toArray(new String[0]);
        }
        else return null;
    }


    private boolean isOperator(String c)
    {
        return (c.equals("+") || c.equals("-") || c.equals("÷") || c.equals("×") );
    }

    private double calcul(String a,String op,String b)
    {
        if( op.equals("+") ) return (Double.parseDouble(a) + Double.parseDouble(b));
        if( op.equals("×") ) return (Double.parseDouble(a) * Double.parseDouble(b));
        if( op.equals("-") ) return (Double.parseDouble(a) - Double.parseDouble(b));
        if( op.equals("÷") ) return (Double.parseDouble(a) / Double.parseDouble(b));
        return 0;
    }

    private Double evaluate(){
        postfix = listToPostFix();
        if (postfix == null)
            return Double.NaN;
        else {
            //System.out.println(Arrays.asList(postfix));
            Stack<String> stack = new Stack<>();
            String Opd1,Opd2,current;
            double value ;
            for (int i = 0; i < postfix.length; i++) {
                if( !isOperator( postfix[i] )) stack.push(postfix[i]);
                else
                {
                    Opd2 = stack.pop();
                    Opd1 = stack.pop();
                    value = calcul(Opd1,postfix[i],Opd2);
                    stack.push("" + value);
                }
            }
            current = stack.peek();
            value = Double.parseDouble(current);
            return value;
        }
    }

    //font utilise
    private Font font;
    //paneaux
    private JPanel btnPanel, screenPanel;

    //buttons des numeros
    private JButton[] numBtn;

    //autres buttons
    private JButton plusBtn,minusBtn,mulBtn,divBtn,clearBtn,delBtn,eqBtn,rParBtn,lParBtn,dotBtn;

    //affichage et saisie
    private JTextField screenTF;

    public Calculator(){
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e) { }
        this.init();
        this.build();
        this.setSize(new Dimension(285,285));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    //init
    private void init() {
        initScreen();
        initButtons();
    }

    private void initScreen() {
        this.screenTF = new JTextField(80);
        this.screenTF.setText(this.text);
        this.screenTF.setEditable(false);
        this.screenTF.setHorizontalAlignment(JTextField.RIGHT);
        this.screenTF.setFont(new Font("Monospaced", Font.BOLD, 19));
        this.screenTF.setColumns(24);

        this.screenPanel = new JPanel();
        this.screenPanel.setBackground(Color.BLACK);
        this.screenPanel.add(this.screenTF);
    }

    private void initButtons() {
        this.font = new Font("Arial", Font.BOLD, 17);
        this.numBtn = new JButton[10];
        for (int i = 0; i < 10; i++) {
            this.numBtn[i] = new JButton("" + i);
            this.numBtn[i].setBackground(Color.DARK_GRAY);
            this.numBtn[i].setForeground(Color.white);
            this.numBtn[i].setFont(font);
            this.numBtn[i].addActionListener(this);
        }

        this.plusBtn = new JButton("+");
        this.plusBtn.addActionListener(this);
        this.plusBtn.setBackground(Color.BLACK);
        this.plusBtn.setForeground(Color.white);
        this.plusBtn.setFont(font);

        this.minusBtn = new JButton("-");
        this.minusBtn.addActionListener(this);
        this.minusBtn.setBackground(Color.BLACK);
        this.minusBtn.setForeground(Color.white);
        this.minusBtn.setFont(font);

        this.mulBtn = new JButton("×");
        this.mulBtn.addActionListener(this);
        this.mulBtn.setBackground(Color.BLACK);
        this.mulBtn.setForeground(Color.white);
        this.mulBtn.setFont(font);

        this.divBtn = new JButton("÷");
        this.divBtn.addActionListener(this);
        this.divBtn.setBackground(Color.BLACK);
        this.divBtn.setForeground(Color.white);
        this.divBtn.setFont(font);

        this.eqBtn = new JButton("=");
        this.eqBtn.addActionListener(this);
        this.eqBtn.setActionCommand("equal");
        this.eqBtn.setBackground(Color.DARK_GRAY);
        this.eqBtn.setForeground(Color.white);
        this.eqBtn.setFont(font);

        this.clearBtn = new JButton("C");
        this.clearBtn.addActionListener(this);
        this.clearBtn.setActionCommand("clear");
        this.clearBtn.setBackground(Color.RED);
        this.clearBtn.setForeground(Color.white);
        this.clearBtn.setFont(font);

        this.delBtn = new JButton("←");
        this.delBtn.addActionListener(this);
        this.delBtn.setActionCommand("delete");
        this.delBtn.setBackground(Color.ORANGE);
        this.delBtn.setForeground(Color.white);
        this.delBtn.setFont(font);

        this.rParBtn = new JButton(")");
        this.rParBtn.addActionListener(this);
        this.rParBtn.setBackground(Color.DARK_GRAY);
        this.rParBtn.setForeground(Color.white);
        this.rParBtn.setFont(font);

        this.lParBtn = new JButton("(");
        this.lParBtn.addActionListener(this);
        this.lParBtn.setBackground(Color.DARK_GRAY);
        this.lParBtn.setForeground(Color.white);
        this.lParBtn.setFont(font);

        this.dotBtn = new JButton(".");
        this.dotBtn.addActionListener(this);
        this.dotBtn.setBackground(Color.DARK_GRAY);
        this.dotBtn.setForeground(Color.white);
        this.dotBtn.setFont(font);

        //ajout des buttons au paneau
        GridLayout layout = new GridLayout(4,5);

        layout.setHgap(10);
        layout.setVgap(10);

        this.btnPanel = new JPanel(layout);
        this.btnPanel.setBackground(Color.BLACK);

        this.btnPanel.setBorder(new EmptyBorder(10,10,10,10));


        //first row
        this.btnPanel.add(numBtn[7]);
        this.btnPanel.add(numBtn[8]);
        this.btnPanel.add(numBtn[9]);
        this.btnPanel.add(plusBtn);
        this.btnPanel.add(clearBtn);

        //2nd row
        this.btnPanel.add(numBtn[4]);
        this.btnPanel.add(numBtn[5]);
        this.btnPanel.add(numBtn[6]);
        this.btnPanel.add(minusBtn);
        this.btnPanel.add(delBtn);

        //3rd row
        this.btnPanel.add(numBtn[1]);
        this.btnPanel.add(numBtn[2]);
        this.btnPanel.add(numBtn[3]);
        this.btnPanel.add(mulBtn);
        this.btnPanel.add(lParBtn);

        //4th row
        this.btnPanel.add(numBtn[0]);
        this.btnPanel.add(dotBtn);
        this.btnPanel.add(eqBtn);
        this.btnPanel.add(divBtn);
        this.btnPanel.add(rParBtn);
    }

    //build
    private void build(){
        this.setBackground(Color.BLACK);

        this.setLayout(new BorderLayout());
        this.add(screenPanel,BorderLayout.NORTH);
        this.add(btnPanel,BorderLayout.CENTER);
        this.pack();
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("clear")) {
            this.text = "";
            this.screenTF.setText(this.text);
            initLists();
            isResult = false;
            return;
        }
        if (actionEvent.getActionCommand().equals("delete")) {
            if (this.screenTF.getText().isEmpty() || this.screenTF.getText().equals("Error !") )
                return;
            this.text = this.text.substring(0, this.text.length() - 1);
            this.screenTF.setText(this.text);
            initLists();
            isResult = false;
            return;
        }
        if (actionEvent.getActionCommand().equals("equal")) {
            initLists();
            if (isResult)
                return;
            isResult = true;
            String result = "" + evaluate();
            text = result.equals("NaN")? "Error !": result;
            this.screenTF.setText(text);
            initLists();
//            list = null;
//            postfix = null;
            return;
        }

        if (!text.equals("Error !")){
            String str = ((JButton)actionEvent.getSource()).getText();
            if ( !isOperator(str) && isResult){
                this.text = "";
                isResult = false;
                initLists();
            }
            this.text += str;
            this.screenTF.setText(this.text);
            initLists();
            isResult = false;
        }



    }
}
