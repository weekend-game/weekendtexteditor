## WeekendTextEditor

A text editor made by extending [BankViewer](https://github.com/weekend-game/bankviewer).

### How to run the program

Download the repository to your computer. Everything you need for the program is located in the app folder. Navigate to the app folder and run the program by double-clicking the WeekendTextEditor.jar file or, if the program doesn't start, double-click the WeekendTextEditor.bat file. If the program doesn't start, download and install Java 11 or later and repeat the steps above.

### How to open a project in Eclipse

In Eclipse, select "Import..." from the "File" menu. In the window that opens, select "Existing projects into workspace." Navigate to the folder with the downloaded repository and click "Finish." The project will open in Eclipse. In the Package Explorer (on the left side of the screen), double-click the WeekendTextEditor.java file. The file will open for editing (in the center of the screen). Run the program by pressing Ctrl+F11 or using your preferred method for running programs in Eclipse.

### How to use the program

This text editor is based on the bank statement viewer and inherits its entire user interface ( [see here](https://weekend-game.github.io/bankviewer.htm#HowToUse) ). Of course, the opened file is now displayed as a text file, not as a table. New features have been added.

The "File" menu now includes the following options: "Create," "Save," and "Save As." They work the same way as in all programs.

The "Edit" menu now includes the "Undo" and "Redo" options. The "Cut" and "Copy" text editing options are active if a text fragment is selected, while the "Paste" option is active only if a copy has been made.

A "Replace" option has been added. When selected, a dialog box appears where you can specify a search string, a replacement string, whether to select case, and specify the search direction. The "Find Next" button works the same as in the search window. The "Replace" button replaces the found string with the replacement string. The "Replace All" button will replace all instances of the search string with the replacement string.

The "View" menu now includes the following options: "Use monospaced font," "Enlarge font," "Decrease font," and "Default font size."

### How the program is written

Details of the program's implementation can be found on the [project page](https://weekend-game.github.io/weekendtexteditor.htm#ProgDescr).

### Results

Excellent practice! The editor can be improved endlessly. But you need to know when to stop.
