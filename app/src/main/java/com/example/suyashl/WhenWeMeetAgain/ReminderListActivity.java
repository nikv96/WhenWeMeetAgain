package com.example.suyashl.WhenWeMeetAgain;

/**
 * Created by SuyashL on 24/1/15.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class ReminderListActivity extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private RemindersDbAdapter mDbHelper;
    private FriendsDbAdapter mDbHelper2;

    Button button2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new RemindersDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());

        mDbHelper2 = new FriendsDbAdapter(this);
        mDbHelper2.open();

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(button2Listener);

        startService(new Intent(this, BluetoothBroadcastReceiver.class));
    }


    private void fillData() {
        Cursor remindersCursor = mDbHelper.fetchAllReminders();
        startManagingCursor(remindersCursor);

        String[] from = new String[]{RemindersDbAdapter.KEY_REMINDER};

        int[] to = new int[]{R.id.text1};

        SimpleCursorAdapter reminders =
                new SimpleCursorAdapter(this, R.layout.reminder_row, remindersCursor, from, to);
        setListAdapter(reminders);
    }

    private Button.OnClickListener button2Listener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            createReminder();
        }};

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.long_press, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteReminder(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createReminder() {
        Intent i = new Intent(this, ReminderEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(RemindersDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}

