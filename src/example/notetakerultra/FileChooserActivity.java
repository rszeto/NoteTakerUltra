/* 
 *  This code specifies the activity responsible for selecting a file for deletion or
 *  opening.
 *  
 *  Author: Ryan Szeto
 *  Last edit date: 1/7/2013
 */

package example.notetakerultra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileChooserActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        
        // create ListView of available note files
        String[] notesFiles = fileList();
        ArrayAdapter<String> optsAdaptr = new ArrayAdapter<String>(
        		this, R.layout.activity_file_chooser_item, notesFiles);
        ListView menuList = (ListView)findViewById(R.id.notesFilesList);
        menuList.setAdapter(optsAdaptr);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View itemClicked, int pos, long id) {
        		TextView chosenView = (TextView) itemClicked;
        		String chosenFile = chosenView.getText().toString();
        		reportChosenFile(chosenFile);
        	}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_file_chooser, menu);
        return true;
    }
    
    // reports file chosen to parent (NoteEditor)
    public void reportChosenFile(String fileName) {
    	Intent result = new Intent();
    	result.putExtra(getString(R.string.key_chosen_file), fileName);
    	setResult(RESULT_OK, result);
    	finish();
    }
}
