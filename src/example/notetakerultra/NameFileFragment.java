/* 
 *  This code specifies a dialog to prompt the user for a file name. The notes
 *  get saved to the file the user enters.
 *  
 *  Author: Ryan Szeto
 *  Last edit date: 1/7/2013
 */

package example.notetakerultra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class NameFileFragment extends DialogFragment implements OnEditorActionListener {

	private EditText et;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        et = new EditText(getActivity()); // field to enter file name
        et.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT 
        		| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        et.setOnEditorActionListener(this);
        et.requestFocus();
        
		AlertDialog d = new AlertDialog.Builder(getActivity())
        	.setTitle(R.string.message_give_filename)
        	.setView(et) // add text field to dialog
        	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				// OK is pressed; saves note in specified file
				public void onClick(DialogInterface dialog, int which) {
					saveFile();
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			}) // create negative button that does nothing
        	.create();
		d.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return d;
    }
	
	// Determines what happens when "Done" is pressed on the keyboard
	public boolean onEditorAction(android.widget.TextView v, int actionId, android.view.KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_DONE) {
			saveFile();
			dismiss();
			return true;
		}
		return false;
	}
	
	// Sends entered file name to NoteEditor, which handles saving the note
	private void saveFile() {
		NoteEditor parent;
		try {
			parent = (NoteEditor)getActivity();
		}
		catch(ClassCastException e) {
			throw new ClassCastException("Parent was not a NoteEditor");
		}
		// get contents of text field, with leading and trailing whitespace omitted
		String fileName = et.getText().toString().trim();
		// note gets saved
		if(!fileName.equals("")) {
			parent.saveNotes(fileName);
			parent.setNotesLoc(fileName);
		}
		else // reject empty file name ("")
			Toast.makeText(getActivity(), 
					getString(R.string.message_save_fail), 
					Toast.LENGTH_SHORT).show();
	}
}