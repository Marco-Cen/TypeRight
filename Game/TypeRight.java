/*--------------------------------------------------------------------------------------*/
/*  TypeRight.java  -  Description                                                       */
/*
Create an educational software/program that can be used as a study aid for elementary school students in grades 6 to 8.
(Programmed in Java, using graphics, graphcial user interface -- possible to use Applet or AWT package in another application)
With this program, students are taught a concept and have the chance to reinforce those new skills and concepts learnt.
Develop the software through a pedagogical approach. Program should be simple, user interactive and easy to follow when used.
Demonstrate object oriented concepts by having methods in classes outside the main method's class, using modularity, constructors
and passing parameters.
				    [Mr.Berry | ICS 4U]                                 */
/*--------------------------------------------------------------------------------------*/
/*  Author: Marco Cen                                                                   */
/*  Date: June 7th 2019                                                                 */
/*--------------------------------------------------------------------------------------*/
/*  Input: Difficulty, falling words in game, Press/click buttons                       */
/*  Output: Pace of game, Information (Words, game), sounds, feedback message, score, life counter */
/*--------------------------------------------------------------------------------------*/

import java.io.*;
import java.util.*;
import java.text.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Image;
import java.applet.AudioClip;

public class TypeRight extends Applet
    implements ActionListener, MouseListener, Runnable, KeyListener //Buttons, Mouse click, annimation, keyboard input
{
    //Global variables
    static final int MAX = 10;  //10 slots to account for wordBank text file
    int userChoice;
    details[] words = new details [MAX]; //Instantiate object
    int lessonChoice;  //User chooses a word for more info
    int rand1 = 0, rand2 = 0, rand3 = 0, rand4 = 0, rand5 = 0; //Random words displayed

    //Double buffering (Reduce flickering)
    Image dbImage;   //doubleBufferImage
    Graphics dbg;  //doubleBufferGraphics

    ////For moving text under title
    private String funText;
    private int xFun, yFun, moveFun;
    Thread t;

    ////For moving text (actual game)
    int life = 3;
    private String enemy, enemy2, enemy3, enemy4, enemy5;
    private int xEnemy, yEnemy, moveEnemy;
    private int xEnemy2, xEnemy3, xEnemy4, xEnemy5;
    private int yEnemy2, yEnemy3, yEnemy4, yEnemy5;
    private int moveEnemy2, moveEnemy3, moveEnemy4, moveEnemy5;
    Thread t2;
    Random random = new Random (); //Random coordinates
    int xRand, xRand2, xRand3, xRand4, xRand5;
    int yRand, yRand2, yRand3, yRand4, yRand5;

    //For textfield (actual game)
    TextField type = new TextField (30);
    String userInput;
    Button checkWord = new Button ("Check");
    String msg;  //Msg that is displayed in pink on side
    int key; //keyboard input
    int countScore = 0; //Score keeping

    //Radio Buttons for difficulty setting
    CheckboxGroup difficulty = new CheckboxGroup ();
    Checkbox easy = new Checkbox ("Easy", difficulty, false);
    Checkbox normal = new Checkbox ("Normal", difficulty, true);  //'True' means Default (Box that is pre-checked)
    Checkbox hard = new Checkbox ("Hard", difficulty, false);

    //Buttons
    Button confirm = new Button ("Confirm Difficulty");  //To confirm difficulty Setting
    Button help = new Button ("Instructions"); //To display info and instruction of game
    Button exit = new Button ("Exit"); //To close program
    Button menu = new Button ("Back to Main Menu"); //Goes menu to previous screen
    Button game = new Button ("Start Game"); //Starts game

    //For text fonts
    Font titleFont = new Font ("Serif", Font.BOLD, 50);
    Font textFont = new Font ("Serif", Font.BOLD, 20);
    Font smallFont = new Font ("Serif", Font.BOLD, 12);
    Font italicFont = new Font ("Comic Sans MS", Font.ITALIC, 15);
    Font lifeFont = new Font ("Monospaced", Font.BOLD, 80);

    //For images & audio
    Image bkgImage;
    private AudioClip bkgAudio;
    private AudioClip correct;

    //Reading in wordBank text file
    static void wordBank (details[] words) throws IOException
    {
	BufferedReader wordFile = new BufferedReader (new FileReader ("wordBank.txt"));

	//Declare variables
	String line = null;
	int counter = 0, i = 0;

	//Read each line of textfile
	while ((line = wordFile.readLine ()) != null)
	{
	    //Sets all information into array (From class methods)
	    words [counter] = new details (); //Initi object
	    words [counter].setWord (line);
	    words [counter].setPartOfSpeech (wordFile.readLine ());
	    words [counter].setPronunciation (wordFile.readLine ());
	    words [counter].setDefinition (wordFile.readLine ());
	    words [counter].setSentence (wordFile.readLine ());
	    counter++;
	}
	wordFile.close ();
    }


    //Initialize
    public void init ()
    {
	setBackground (Color.pink);  //Bkg colour
	userChoice = 0; //Default setting (Program opened/User doesnt do anything)

	add (exit);
	add (easy);
	add (normal);
	add (hard);
	add (confirm);
	add (help);
	add (menu);
	add (game);
	add (checkWord);
	add (type);

	//Allows button to be useable
	confirm.addActionListener (this);
	help.addActionListener (this);
	exit.addActionListener (this);
	menu.addActionListener (this);
	game.addActionListener (this);
	checkWord.addActionListener (this);
	addMouseListener (this); //To allow mouse click to work
	type.addKeyListener (this); //To allow keyboard input

	//Audio
	bkgAudio = getAudioClip (getDocumentBase (), "Wet Hands.wav"); //Bkg music
	bkgAudio.play (); //Other songs: "Minecraft", "Sweden", "Wet Hands", |Lullaby"
	bkgAudio.loop ();
	correct = getAudioClip (getDocumentBase (), "Correct.wav"); //Correct word input

	//Random generator for enemy coordinates
	xRand = random.nextInt (100); //850 if play full screen
	xRand2 = random.nextInt (100) + 150;
	xRand3 = random.nextInt (100) + 300;
	xRand4 = random.nextInt (100) + 450;
	xRand5 = random.nextInt (100) + 500; //700 if side screen

	////For moving text(under title)
	funText = "[NEW WORDS! TPYING! FUN! PLAY NOW!]";
	xFun = 300;
	yFun = 127;
	moveFun = 1;

	////Moving actual game text (Enemy)
	xEnemy = xRand;
	xEnemy2 = xRand2;
	xEnemy3 = xRand3;
	xEnemy4 = xRand4;
	xEnemy5 = xRand5;
	yEnemy = -10;
	yEnemy2 = -10;
	yEnemy3 = -10;
	yEnemy4 = -10;
	yEnemy5 = -10;
	moveEnemy = 1;
	moveEnemy2 = 1;
	moveEnemy3 = 1;
	moveEnemy4 = 1;
	moveEnemy5 = 1;

	//Create thread
	t = new Thread (this, "MyThread");  //Text under title
	t2 = new Thread (this, "MyThread");  //Actual game

	//Start thread
	t.start ();
	t2.start ();
    }


    //For Double Buffer
    public void update (Graphics g)
    {
	dbImage = createImage (900, 900); //Create a copy of screen
	dbg = dbImage.getGraphics (); //Copy of all the graphics
	paint (dbg); //Paint graphics over
	g.drawImage (dbImage, 0, 0, this); //draw image of dbImage
    }


    ////Update x coordinate (For moving text)
    public void updateFunText ()
    {
	xFun = xFun + 10 * moveFun;
	if (xFun > 410)
	{
	    moveFun = -1;  //Move text back to left
	}

	if (xFun < 185)
	{
	    moveFun = 1;  //Move text back to right
	}
    }


    ////Moving enemey texts  (Update location of words randomly)
    public void updateEnemy ()
    {
	yEnemy = yEnemy + 10 * moveEnemy;  //Send first word automatically

	//All separate if statements
	if (yEnemy > yRand || userInput.equals (enemy) || moveEnemy == -1)  //If y coordinate (as word falls) goes past random (Y coordinate) setted, send in next word
	{
	    yEnemy2 = yEnemy2 + 10 * moveEnemy2;
	}
	if (yEnemy2 > yRand2 || userInput.equals (enemy2) || moveEnemy2 == -1) //Or if user types in correct word, send next word
	{
	    yEnemy3 = yEnemy3 + 10 * moveEnemy3;
	}
	if (yEnemy3 > yRand3 || userInput.equals (enemy3) || moveEnemy3 == -1) //Or if moves back up, send next word
	{
	    yEnemy4 = yEnemy4 + 10 * moveEnemy4;
	}
	if (yEnemy4 > yRand4 || userInput.equals (enemy4) || moveEnemy4 == -1)
	{
	    yEnemy5 = yEnemy5 + 10 * moveEnemy5;
	}
    }


    ////run (For moving text)
    public void run ()
    {
	while (true)
	{
	    if (userChoice == 0 || userChoice == 6 || userChoice == 4)
	    {
		repaint (); //Repaint to give illusion moving
		updateFunText ();
		try
		{
		    Thread.sleep (250); //Creating pause of x miliseconds
		}
		catch (InterruptedException ie)
		{
		    System.out.println (ie);
		}
	    }

	    if (lessonChoice == 6)
	    {
		//When to break out of while(true) loop (nothing happens within these parameters)
		if (life == 0)
		{
		}
		else if ((yEnemy5 > 740 || yEnemy4 > 740 || yEnemy3 > 740 || yEnemy2 > 740 || yEnemy > 740) && countScore >= 3 && life != 0) //Lose all life or if win game (More than 3 words, life not 0, last word falls past)
		{
		    correct.play (); //>740, if past Exit button, then End End game
		}
		else
		{
		    repaint ();
		    updateEnemy ();
		    //Life counter if word passes out of image
		    if (yEnemy > 487 && yEnemy < 494) //If word crosses specific y cordinate on screen, count as life loss
		    {
			life = life - 1;
			Toolkit.getDefaultToolkit ().beep ();
		    }

		    else if (yEnemy2 > 487 && yEnemy2 < 494)
		    {
			life = life - 1;
			Toolkit.getDefaultToolkit ().beep ();
		    }
		    else if (yEnemy3 > 487 && yEnemy3 < 494)
		    {
			life = life - 1;
			Toolkit.getDefaultToolkit ().beep ();
		    }
		    else if (yEnemy4 > 487 && yEnemy4 < 494)
		    {
			life = life - 1;
			Toolkit.getDefaultToolkit ().beep ();
		    }
		    else if (yEnemy5 > 487 && yEnemy5 < 494)
		    {
			life = life - 1;
			Toolkit.getDefaultToolkit ().beep ();
		    }


		    if (userChoice == 1)  //Speed for easy
		    {
			try
			{
			    Thread.sleep (700);  //Rate at which word falls
			}
			catch (InterruptedException ie)
			{
			    System.out.println (ie);
			}
		    }

		    else if (userChoice == 3) //Speed for hard
		    {
			try
			{
			    Thread.sleep (200);
			}
			catch (InterruptedException ie)
			{
			    System.out.println (ie);
			}
		    }

		    else  //Default speed (Default is set on Normal)
		    {
			try
			{
			    Thread.sleep (400);
			}
			catch (InterruptedException ie)
			{
			    System.out.println (ie);
			}
		    }
		}
	    }
	}
    }


    public void paint (Graphics g)
    {
	//Move Button location
	exit.setLocation (700, 735);
	easy.setLocation (230, 210);
	normal.setLocation (230, 260);
	hard.setLocation (230, 310);
	confirm.setLocation (230, 338);
	help.setLocation (380, 153); //600, 25
	menu.setLocation (620, 680);
	game.setLocation (350, 630);
	checkWord.setLocation (430, 660);
	type.setLocation (240, 630);

	if (userChoice == 0 || userChoice == 6 || userChoice == 4)
	{
	    g.clearRect (0, 0, 900, 900); //Clear screen
	    g.drawString ("Created by: Marco Cen", 570, 750); //Credit

	    //Title text
	    g.setFont (titleFont);
	    g.setColor (Color.blue);
	    g.drawString ("Welcome to TypeRight!", 200, 100);

	    //Text under title
	    g.setFont (italicFont);
	    g.setColor (Color.magenta);
	    g.drawString (funText, xFun, yFun);  //////For moving text

	    //Select difficulty
	    g.setFont (textFont);
	    g.setColor (Color.black);
	    g.drawString ("Select a Difficulty: ", 130, 185);

	    //Visibility of buttons
	    easy.setVisible (true);
	    normal.setVisible (true);
	    hard.setVisible (true);
	    confirm.setVisible (true);
	    help.setVisible (true);

	    menu.setVisible (false);
	    game.setVisible (false);
	    checkWord.setVisible (false);
	    type.setVisible (false);

	    //Replayability
	    yEnemy = -10; //Resets y coordinates
	    yEnemy2 = -10;
	    yEnemy3 = -10;
	    yEnemy4 = -10;
	    yEnemy5 = -10;
	    life = 3; //Resets life counter
	    countScore = 0;
	    moveEnemy = 1;
	    moveEnemy2 = 1;
	    moveEnemy3 = 1;
	    moveEnemy4 = 1;
	    moveEnemy5 = 1;
	}

	//Button activation
	switch (userChoice)
	{
	    case 1:  //Easy difficulty
		//Words fall at random intervals (When word reaches certain point, enter next word)
		yRand = random.nextInt (600); //Bigger the # = more time before next word appears
		yRand2 = random.nextInt (600);
		yRand3 = random.nextInt (600);
		yRand4 = random.nextInt (600);
		yRand5 = random.nextInt (600);
		break;

	    case 2: //Normal difficulty
		yRand = random.nextInt (500);
		yRand2 = random.nextInt (500);
		yRand3 = random.nextInt (500);
		yRand4 = random.nextInt (500);
		yRand5 = random.nextInt (500);
		break;

	    case 3: //Hard difficulty
		yRand = random.nextInt (300);
		yRand2 = random.nextInt (300);
		yRand3 = random.nextInt (300);
		yRand4 = random.nextInt (300);
		yRand5 = random.nextInt (300);
		break;

	    case 4: //Help (Instructions button)
		g.drawRect (380, 175, 384, 200);
		g.drawString ("                :", 380, 174);
		g.setFont (smallFont);
		g.setColor (Color.magenta);
		g.drawString ("Its super easy, just follow these simple steps", 450, 194);
		g.setColor (Color.blue);
		g.drawString ("How to play the game: ", 390, 220);
		g.drawString ("1. Confirm a difficulty (How fast the game will play)", 390, 240);
		g.drawString ("2. Learn new words (Each word is a button filled with information!)", 390, 260);
		g.drawString ("3. After studying each word, press 'Start Game'", 390, 280);
		g.drawString ("4. Game starts! Good luck, have fun!", 390, 300);
		g.drawString ("Game: Type the words you see falling down the screen in the textbox.", 390, 335);
		g.drawString ("             Press 'Check' to remove the falling word.", 390, 350);
		g.drawString ("             Dont let a word fall out of the sky 3 times or you lose the game!", 390, 365);
		break;

	    case 5: //Exit
		System.out.println (" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println ("| Thanks for playing TYPERIGHT! Hope you had a blast fun! See you again soon! |");
		System.out.println (" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		//Stop program and close window
		try
		{
		    Thread.sleep (2000);  //Bigger #= longer wait time
		}
		catch (InterruptedException ie)
		{
		}
		System.exit (0);
		break;

		//    case 6: //Main menu button
		//
		//              break;

	    case 7: //Confirm difficulty
		int x = 50, y = 100, y2 = 75, y3 = 73;  //Higher X: right Higher Y: lower

		//Remove buttons
		easy.setVisible (false);
		normal.setVisible (false);
		hard.setVisible (false);
		confirm.setVisible (false);
		help.setVisible (false);

		menu.setVisible (true);

		// g.drawRect (125, 55, 700, 400); //Test to see what area being covered, [(0, 0, 900, 900)] covers entire screen
		g.clearRect (125, 55, 700, 400);  //Draws rectangle ontop of main menu screen

		//Display texts on screen
		g.setFont (italicFont);
		if (easy.getState () == true)
		{
		    g.setColor (Color.green);
		    g.drawString ("<<<The Difficulty is set on [EASY]!>>>", 360, 66);
		}

		else if (normal.getState () == true)
		{
		    g.setColor (Color.gray);
		    g.drawString ("<<<The Difficulty is set on [NORMAL]!>>>", 360, 66);
		}

		else if (hard.getState () == true)
		{
		    g.setColor (Color.red);
		    g.drawString ("<<<The Difficulty is set on [HARD]!>>>", 360, 66);
		}

		if (lessonChoice != 6)  //Hide Start Game button after confirm
		{
		    game.setVisible (true);
		}

		g.setColor (Color.magenta);
		g.drawString ("Learn These Words: (Click each word)", 10, 65);

		g.setFont (titleFont);
		g.setColor (Color.blue);
		g.drawString ("LESSON PAGE!", 210, 40);

		g.setFont (textFont);
		//Randomly select words from textFile and display
		try
		{
		    wordBank (words);
		}
		catch (Throwable t)
		{
		    t.printStackTrace ();  //Print errors in console
		}

		//Validation so no same word randomly selected (Replaces words that are same)
		while (rand1 == rand2 || rand1 == rand3 || rand1 == rand4 || rand1 == rand5 || rand2 == rand3
			|| rand2 == rand4 || rand2 == rand5 || rand3 == rand4 || rand3 == rand5 || rand4 == rand5)
		{
		    rand1 = (int) (100 * Math.random ()) % MAX;
		    rand2 = (int) (100 * Math.random ()) % MAX;
		    rand3 = (int) (100 * Math.random ()) % MAX;
		    rand4 = (int) (100 * Math.random ()) % MAX;
		    rand5 = (int) (100 * Math.random ()) % MAX;
		}

		//Draw white rectangle (For mouse pressed)
		for (int i = 0 ; i < 5 ; i++)
		{
		    //Border of boxes drawn
		    g.setColor (Color.magenta);
		    g.drawRect (38, y3, 127, 37);
		    y3 = y3 + 110;

		    //Fill in colour for boxes drawn
		    g.setColor (Color.white);
		    g.fillRect (40, y2, 125, 35);
		    y2 = y2 + 110;
		}

		//Print out randomly selected words
		g.setColor (Color.blue);
		g.drawString (words [rand1].getWord (), x, y);
		g.drawString (words [rand2].getWord (), x, y + 110);
		g.drawString (words [rand3].getWord (), x, y + 220);
		g.drawString (words [rand4].getWord (), x, y + 330);
		g.drawString (words [rand5].getWord (), x, y + 440);
		break;
	}

	g.setColor (Color.black);
	switch (lessonChoice)
	{
	    case 1: //Info for Rand1
		g.setFont (smallFont);
		g.drawString (words [rand1].getPartOfSpeech (), 170, 100);
		g.drawString (words [rand1].getPronunciation (), 250, 100);
		g.setFont (textFont);
		g.drawString (words [rand1].getDefinition (), 40, 132);
		g.drawString (words [rand1].getSentence (), 25, 163);
		break;

	    case 2: //Info for Rand2
		g.setFont (smallFont);
		g.drawString (words [rand2].getPartOfSpeech (), 170, 210);
		g.drawString (words [rand2].getPronunciation (), 250, 210);
		g.setFont (textFont);
		g.drawString (words [rand2].getDefinition (), 40, 242);
		g.drawString (words [rand2].getSentence (), 25, 273);
		break;

	    case 3: //Info for Rand3
		g.setFont (smallFont);
		g.drawString (words [rand3].getPartOfSpeech (), 170, 320);
		g.drawString (words [rand3].getPronunciation (), 250, 320);
		g.setFont (textFont);
		g.drawString (words [rand3].getDefinition (), 40, 352);
		g.drawString (words [rand3].getSentence (), 25, 383);
		break;

	    case 4: //Info for Rand4
		g.setFont (smallFont);
		g.drawString (words [rand4].getPartOfSpeech (), 170, 430);
		g.drawString (words [rand4].getPronunciation (), 250, 430);
		g.setFont (textFont);
		g.drawString (words [rand4].getDefinition (), 40, 462);
		g.drawString (words [rand4].getSentence (), 25, 493);
		break;

	    case 5: //Info for Rand5
		g.setFont (smallFont);
		g.drawString (words [rand5].getPartOfSpeech (), 170, 540);
		g.drawString (words [rand5].getPronunciation (), 250, 540);
		g.setFont (textFont);
		g.drawString (words [rand5].getDefinition (), 40, 572);
		g.drawString (words [rand5].getSentence (), 25, 603);
		break;

	    case 6: //Start Game
		game.setVisible (false);
		type.setVisible (true);
		checkWord.setVisible (true);
		g.clearRect (0, 0, 900, 800);  //Draws rectangle ontop of main menu screen
		g.drawImage (bkgImage, 0, 0, this);

		//Draw in falling words
		g.setFont (textFont);
		g.setColor (Color.white);
		g.drawString (enemy, xEnemy, yEnemy);
		g.drawString (enemy2, xEnemy2, yEnemy2);
		g.drawString (enemy3, xEnemy3, yEnemy3);
		g.drawString (enemy4, xEnemy4, yEnemy4);
		g.drawString (enemy5, xEnemy5, yEnemy5);

		//To cover falling words (After cross line (Outside of picture) -- 487 y coordinate)
		g.setColor (Color.black);
		//    g.drawRect (0, 480, 850, 400); //For test only: 850 perfectly aligned with image (length wise)
		g.setColor (Color.pink);
		g.fillRect (0, 480, 850, 400); //Cover entire bottom half (Hide falling words)

		//Lesson Page
		g.setColor (Color.blue);
		g.drawRect (592, 498, 140, 40);
		g.setColor (Color.white);
		g.fillRect (594, 500, 138, 38);
		g.setColor (Color.black);
		g.drawString ("Lesson Page", 608, 524);

		//Instructions
		g.drawString ("Score:", 10, 500); //Score  (20, 525)
		g.setFont (smallFont);
		g.setColor (Color.blue);
		g.drawString ("[ONLY 3 LIVES]", 40, 655);
		g.drawString ("[Click to review words]", 603, 551);
		g.drawString ("[Press 'Enter' or Click 'Check']", 255, 666);
		g.drawString ("(Case Sensitive)", 480, 645);
		g.setFont (italicFont);
		g.setColor (Color.magenta);
		g.drawString ("<<< Don't let it fall out of the sky! >>>", 235, 520);
		g.drawString ("<<< Type the falling words! >>>", 260, 555);

		//Life counter
		g.setColor (Color.black);
		g.drawString ("Life Counter: ", 10, 575);
		g.drawRect (10, 580, 155, 60); //Outline box
		g.setColor (Color.gray);
		g.fillRect (12, 582, 152, 57); //perfectly covers life box

		//Message prompt (Correct/Wrong)
		g.setFont (titleFont);
		g.setColor (Color.magenta);
		g.drawString (msg, 238, 615);  //285, 540

		//Score Board
		g.setFont (textFont);
		g.setColor (Color.white);
		if (countScore == 0)
		{
		    g.drawString ("0000", 70, 500); //Old: (80, 525)
		}
		else if (countScore != 0)
		{
		    g.drawString ((countScore + "000"), 70, 500);
		}

		if (((yEnemy5 > 735 || yEnemy4 > 735 | yEnemy3 > 735 || yEnemy2 > 735 || yEnemy > 735) && countScore >= 3 && life != 0)  //>630 (Exit button and up) then display prompt
			|| moveEnemy == -1 && moveEnemy2 == -1 && moveEnemy3 == -1 && moveEnemy4 == -1 && moveEnemy5 == -1)
		{
		    g.setFont (titleFont);
		    g.setColor (Color.green);
		    g.drawString ("WINNER", 250, 250);
		    g.setFont (smallFont);
		    g.drawString ("[Click 'Lesson Page' OR 'Back to Main Menu']", 235, 270);
		}

		//Life display
		g.setFont (lifeFont);
		g.setColor (Color.red);
		if (life == 2)
		{
		    g.drawString ("X", 13, 633);
		}

		else if (life == 1)
		{
		    g.drawString ("X", 13, 633);
		    g.drawString ("X", 63, 633);
		}
		else if (life == 0)
		{
		    Toolkit.getDefaultToolkit ().beep (); //Make beep noise
		    g.drawString ("X", 13, 633);
		    g.drawString ("X", 63, 633);
		    g.drawString ("X", 113, 633);

		    g.setFont (titleFont);
		    g.drawString ("GAME OVER", 250, 250);
		    g.setFont (smallFont);
		    g.drawString ("[Click 'Lesson Page' OR 'Back to Main Menu']", 285, 270);
		}
		break;

	    case 7: //To clear words after mouse click each word
		g.setFont (smallFont);
		g.drawString ("", 170, 540);
		break;

	    case 8: //Used after press Lesson Page
		checkWord.setVisible (false);
		type.setVisible (false);
		break;
	}
    }


    public void actionPerformed (ActionEvent evt)  //Setting each button press to switch case number
    {
	//For radio buttons
	if (easy.getState () == true)
	{
	    userChoice = 1;
	}


	else if (normal.getState () == true)
	{
	    userChoice = 2;
	}


	else if (hard.getState () == true)
	{
	    userChoice = 3;
	}

	//For regular buttons
	if (evt.getSource () == help)
	{
	    userChoice = 4;
	}


	else if (evt.getSource () == exit)
	{
	    userChoice = 5;
	}


	else if (evt.getSource () == menu)
	{
	    userChoice = 6;
	    lessonChoice = 7;
	}


	else if (evt.getSource () == confirm)
	{
	    userChoice = 7;

	    //Randomize
	    rand1 = (int) (100 * Math.random ()) % MAX;
	    rand2 = (int) (100 * Math.random ()) % MAX;
	    rand3 = (int) (100 * Math.random ()) % MAX;
	    rand4 = (int) (100 * Math.random ()) % MAX;
	    rand5 = (int) (100 * Math.random ()) % MAX;
	}


	else if (evt.getSource () == game || evt.getSource () == checkWord)
	{
	    userInput = type.getText ();  //What user types in
	    type.setText ("");
	    msg = "TypeRight!"; //Placed here so after accept any input in textbox again after check/enter, will display msg

	    //Assign each falling word to word seen earlier
	    enemy = words [rand1].getWord ();
	    enemy2 = words [rand2].getWord ();
	    enemy3 = words [rand3].getWord ();
	    enemy4 = words [rand4].getWord ();
	    enemy5 = words [rand5].getWord ();

	    lessonChoice = 6;
	    bkgImage = getImage (getCodeBase (), "skybkg.jpg");  //bkg image

	    //User input text box
	    if (userInput.equals ("")) //Nothing typed (allow for 'typeright' text to display)
	    {
	    }
	    else if (userInput.equals (enemy) && yEnemy < 487) //All in separate statements bc want different encouraging feedback output msg
	    {
		msg = "Nice One!";
		moveEnemy = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy2) && yEnemy2 < 487)
	    {
		msg = "Great!";
		moveEnemy2 = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy3) && yEnemy3 < 487)
	    {
		msg = "Amazing!";
		moveEnemy3 = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy4) && yEnemy4 < 487)
	    {
		msg = "Wow!";
		moveEnemy4 = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy5) && yEnemy5 < 487)
	    {
		msg = "Coolio!";
		moveEnemy5 = -1;
		countScore++;
		correct.play ();
	    }
	    else  //If type anything else that arent the words on screen or nothing typed
	    {
		msg = "Oopsie.";
		Toolkit.getDefaultToolkit ().beep ();
	    }
	}
	repaint ();
    }


    public void keyTyped (KeyEvent e)
    {
    }


    public void keyReleased (KeyEvent e)
    {
    }


    public void keyPressed (KeyEvent e)
    {
	userInput = type.getText ();  //Get user input in textfield
	lessonChoice = 6;
	msg = "TypeRight!";

	key = e.getKeyCode ();
	if (key == KeyEvent.VK_ENTER)
	{
	    type.setText (""); //Clear textbox each time press enter

	    //User input text box
	    if (userInput.equals (enemy) && yEnemy < 487)  //First random word
	    {
		msg = "Nice One!";
		moveEnemy = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy2) && yEnemy2 < 487)
	    {
		msg = "Great!";
		moveEnemy2 = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy3) && yEnemy3 < 487)
	    {
		msg = "Amazing!";
		moveEnemy3 = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy4) && yEnemy4 < 487)
	    {
		msg = "Wow!";
		moveEnemy4 = -1;
		countScore++;
		correct.play ();
	    }
	    else if (userInput.equals (enemy5) && yEnemy5 < 487)
	    {
		msg = "Coolio!";
		moveEnemy5 = -1;
		countScore++;
		correct.play ();
	    }
	    else  // When nothing entered or anything else thats not words on screen
	    {
		msg = "Oopsie.";
		Toolkit.getDefaultToolkit ().beep ();
	    }


	}
    }


    public void mouseEntered (MouseEvent e)
    {
    }


    public void mouseReleased (MouseEvent e)
    {
    }


    public void mousePressed (MouseEvent e)
    {
    }


    public void mouseExited (MouseEvent e)
    {
    }


    public void mouseClicked (MouseEvent e)
    {
	boolean change = false;
	//(40, 75, 125, 35) for first box/word  [40+125 =e.getX 2, 75+35 = e.getY 2]
	if (((e.getX () >= 40) && (e.getX () <= 165)) && ((e.getY () >= 75) && (e.getY () <= 110)))
	{
	    lessonChoice = 1;
	    change = true;
	    //For testing purposes: System.exit(0);
	}

	else if (((e.getX () >= 40) && (e.getX () <= 165)) && ((e.getY () >= 185) && (e.getY () <= 220)))
	{
	    lessonChoice = 2;
	    change = true;
	}

	else if (((e.getX () >= 40) && (e.getX () <= 165)) && ((e.getY () >= 295) && (e.getY () <= 330)))
	{
	    lessonChoice = 3;
	    change = true;
	}

	else if (((e.getX () >= 40) && (e.getX () <= 165)) && ((e.getY () >= 405) && (e.getY () <= 440)))
	{
	    lessonChoice = 4;
	    change = true;
	}

	else if (((e.getX () >= 40) && (e.getX () <= 165)) && ((e.getY () >= 515) && (e.getY () <= 550)))
	{
	    lessonChoice = 5;
	    change = true;
	}

	//Go back to lesson page
	//g.drawRect (592, 498, 140, 40);
	if (((e.getX () >= 592) && (e.getX () <= 732)) && ((e.getY () >= 140) && (e.getY () <= 538)))
	{
	    lessonChoice = 8;
	    userChoice = 7;
	    change = true;
	}

	if (change)
	{
	    repaint ();
	}
    }


    public static void main (String str[]) throws IOException
    {
	BufferedReader stdin = new BufferedReader (new InputStreamReader (System.in));
	DecimalFormat df = new DecimalFormat ("#");
    }
}


