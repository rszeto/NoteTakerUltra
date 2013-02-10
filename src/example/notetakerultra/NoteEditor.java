/* 
 *  This code contains the main logic for the note editor application. This activity
 *  displays the notes and gives various options such as saving, opening, and deleting
 *  files.
 *  
 *  Author: Ryan Szeto
 *  Last edit date: 1/7/2013
 */

package example.notetakerultra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoteEditor extends FragmentActivity {
	
	// request codes for onActivityResult()
	private static final int OPEN_FILE = 1;
	private static final int DELETE_FILE = 2;
	
	private TextView fileNameView; // View displaying name of open file
	private EditText notesView; // View displaying note contents
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        
        // initializes fileNameView and notesView
        fileNameView = (TextView)findViewById(R.id.fileNameTextView);
        notesView = (EditText)findViewById(R.id.notes_text);
        
        // opens previously opened file, if any
    	String notesLoc = getNotesLoc();
    	if(notesLoc != null)
    		openNotes(notesLoc);
    	else
    		loadBlankTemplate();
    }
    
    // Updates the views whenever there is a transition to this activity
    @Override
    public void onStart() {
    	super.onStart();
    	updateLookAndFeel();
    }
    
    // Updates the note contents view to what is specified in the preferences
	private void updateLookAndFeel() {
		SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(this);
		int textSize = Integer.parseInt(sprefs.getString(
				getString(R.string.key_pref_text_size), "20"));
		notesView.setTextSize(textSize);
		// updates the typeface (options specified in @array/text_fonts)
		notesView.setTypeface(getTypeface(sprefs.getString(
				getString(R.string.key_pref_font), "1")));
		// updates color scheme (options specified in @array/textbox_colors)
		setTextboxColors(notesView, sprefs.getString(
				getString(R.string.key_pref_color), ""));
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_note_editor, menu);
        return true;
    }

    // Set location to save current notes; if location is null,
    // a dialog requesting save location will appear upon saving
	void setNotesLoc(String fileName) {
		// save location
		SharedPreferences sprefs = getPreferences(Context.MODE_PRIVATE);
    	Editor e = sprefs.edit();
    	e.putString(getString(R.string.key_notes_location), fileName);
    	e.commit();
    	// update file name view
    	if(fileName != null)
    		fileNameView.setText(fileName);
    	else
    		fileNameView.setText(getString(R.string.message_new_file));
	}
	
	// Gets the file that is currently open
	private String getNotesLoc() {
		SharedPreferences sprefs = getPreferences(Context.MODE_PRIVATE);
		return sprefs.getString(getString(R.string.key_notes_location), null);
	}

	// Clears the note content view and sets the save location to null, indicating 
	// the notes will be in a new, nonexistent file
	private void loadBlankTemplate() {
		notesView.setText(null);
    	setNotesLoc(null);
    	Toast.makeText(this, 
    			getString(R.string.message_new_note_success), 
    			Toast.LENGTH_SHORT).show();
	}
    
	// Reads the specified file and loads the content into the note views
    private void openNotes(String fileName) {
    	if(fileName == null)
    		throw new RuntimeException("Attempted to open file w/ null file name");
    	loadFileNameAndContent(fileName);
    	setNotesLoc(fileName);
    	
    	// build load success message, then display it in a toast
    	String loadSuccess = String.format(
				getString(R.string.message_load_success), fileName);
		Toast.makeText(this, loadSuccess, Toast.LENGTH_SHORT).show();
    }
	
    // Opens the specified file and writes the note contents to it
    void saveNotes(String fileName) {
    	String notesContents = notesView.getText().toString();
    	FileOutputStream ostream;
    	try {
    		// open file and write contents
    		ostream = openFileOutput(fileName, Context.MODE_PRIVATE);
    		ostream.write(notesContents.getBytes());
    		ostream.close();
    		
    		// build save success message, then display it in a toast
    		String successMsg = String.format(
    				getString(R.string.message_save_success, fileName));
    		Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    // Delete the specified file, and load a blank template if deleted file was the
    // previously open file
    private void deleteNotes(String fileName) {
    	if(fileName == null)
    		throw new RuntimeException("Attempted to delete file w/ null file name");
    	File file = new File(getFilesDir() + "/" + fileName);
    	if(file.delete()) {
    		// build delete success message, display it in a toast
    		String succMsg = String.format(
    				getString(R.string.message_delete_success), fileName);
    		Toast.makeText(this, succMsg, Toast.LENGTH_SHORT).show();
    		
    		// load blank template if deleted file was the open file
    		if(fileName.equals(getNotesLoc())) {
    			loadBlankTemplate();
    		}
    	}
    	else {
    		// show toast saying that deletion failed (shouldn't be reached)
    		String failMsg = String.format(
    				getString(R.string.message_delete_fail), fileName);
    		Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show();
    	}
    }
    
    // Updates text views to correspond to specified file
	private void loadFileNameAndContent(String fileName) {
		fileNameView.setText(fileName);
    	String content = getContent(fileName);
    	notesView.setText(content);
	}
    
	// Open and return content in specified file
    private String getContent(String fileName) {
    	String content = null; // return value
    	try {
    		// open file and build contents
    		InputStream istream = openFileInput(fileName);
    		BufferedReader br = new BufferedReader(new InputStreamReader(istream));
    		StringBuilder contentBuilder = new StringBuilder();
    		String line = br.readLine();
    		while(line != null) {
    			contentBuilder.append(line);
    			contentBuilder.append("\n");
    			line = br.readLine();
    		}
    		content = contentBuilder.toString(); // set return value
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return content;
    }
    
    // Method called when user presses the Save button
    public void onSaveButtonClick(View view) {
    	// get location of open file
    	SharedPreferences sprefs = getPreferences(Context.MODE_PRIVATE);
    	String notesLoc = sprefs.getString(getString(R.string.key_notes_location), null);
    	if(notesLoc == null) {
    		// no save location found; prompt location input with a dialog
    		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
    		if(prev != null)
    			ft.remove(prev);
    		ft.addToBackStack(null);
    		DialogFragment nff = new NameFileFragment();
    		nff.show(ft, "dialog");
    	}
    	else
    		saveNotes(notesLoc); // save notes to open location
    }
    
    // Run settings activity
    public void openSettings() {
    	Intent intent = new Intent(this, example.notetakerultra.SettingsActivity.class);
    	startActivity(intent);
    }
    
    // Set actions that occur when options from the menu are selected
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.menu_new:
    		loadBlankTemplate();
    		return true;
    	case R.id.menu_settings:
    		openSettings();
    		return true;
    	case R.id.menu_delete:
    		deleteFileSelect();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    // Open the file selector; purpose is indicated by the request code
    public void selectFile(int requestCode) {
    	Intent intent = new Intent(this, example.notetakerultra.FileChooserActivity.class);
    	startActivityForResult(intent, requestCode);
    }
    
    // Select a file for deletion
    public void deleteFileSelect() {
    	selectFile(DELETE_FILE);
    }
    
    // Select a file to open
    public void openFileSelect(View view) {
    	selectFile(OPEN_FILE);
    }
    
    // Handle result requests; they will come from selectFile()
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_OK) {
    		String file;
    		switch(requestCode) {
    		case OPEN_FILE:
    			file = data.getExtras().getString(getString(R.string.key_chosen_file));
        		openNotes(file);
        		return;
    		case DELETE_FILE:
    			file = data.getExtras().getString(getString(R.string.key_chosen_file));
    			deleteNotes(file);
    			return;
    		}
    	}
    }
    
    // Sets the color scheme of the note content view
	private void setTextboxColors(EditText notesText, String colors) {
		// default colors (black on gray)
		int textColor = getResources().getColor(R.color.notepad_black);
		int bgColor = getResources().getColor(R.color.notepad_gray);
		if(colors.equals("Black on yellow")) {
			textColor = getResources().getColor(R.color.notepad_black);
			bgColor = getResources().getColor(R.color.notepad_yellow);
		}
		else if(colors.equals("White on black")) {
			textColor = getResources().getColor(R.color.notepad_white);
			bgColor = getResources().getColor(R.color.notepad_black);
		}
		notesText.setTextColor(textColor);
		notesText.setBackgroundColor(bgColor);
	}
	
	// Get the typeface indicated by the values in @array/text_fonts_vals
	private Typeface getTypeface(String typefaceLabel) {
		switch(Integer.parseInt(typefaceLabel)) {
			case 1: return Typeface.DEFAULT;
			case 2: return Typeface.DEFAULT_BOLD;
			case 3: return Typeface.MONOSPACE;
			case 4: return Typeface.SANS_SERIF;
			case 5: return Typeface.SERIF;
			default: throw new RuntimeException("No typeface for label " + typefaceLabel);
		}
	}

}
