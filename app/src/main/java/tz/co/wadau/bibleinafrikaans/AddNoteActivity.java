package tz.co.wadau.bibleinafrikaans;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import tz.co.wadau.bibleinafrikaans.customviews.colorpicker.ColorPickerDialog;
import tz.co.wadau.bibleinafrikaans.customviews.colorpicker.ColorPickerSwatch;
import tz.co.wadau.bibleinafrikaans.data.DbHelper;
import tz.co.wadau.bibleinafrikaans.model.Note;
import tz.co.wadau.bibleinafrikaans.utils.Utils;

public class AddNoteActivity extends AppCompatActivity {

    private final String TAG = AddNoteActivity.class.getSimpleName();

    private DbHelper db;
    private EditText editText;
    private String createdDate;
    private Long mNoteId;
    private String initialText;
    private int selectedColor = Color.parseColor("#FDFC9D");
    private LinearLayout linearLayout;
    long noteId;
    public static final String NOTE_DATE = "tz.co.wadau.bibleinafrikaans.NOW";
    public static final String NOTE_ID = "tz.co.wadau.bibleinafrikaans.NOTE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        db = new DbHelper(getApplicationContext());
        Intent startedIntent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_note_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionbar = getSupportActionBar();

        editText = (EditText) findViewById(R.id.note_text);
        linearLayout = (LinearLayout) findViewById(R.id.activity_add_note);
//        createdDate = startedIntent.getStringExtra(NOTE_DATE);
        mNoteId = startedIntent.getLongExtra(NOTE_ID, 0);
        mActionbar.setTitle(Utils.formatToSystemDateFormat(this));

        // Id is present on this day show it for editing
        if (mNoteId > 0) {
            Note note = db.getNoteById(mNoteId);
            editText.setText(note.getText());
            selectedColor = Color.parseColor(note.getColor());
        }

        linearLayout.setBackgroundColor(selectedColor);

        initialText = editText.getText().toString();

        // Fixing new line does not have correct linespacing
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                float add = editText.getLineSpacingExtra();
                float mult = editText.getLineSpacingMultiplier();
                editText.setLineSpacing(0f, 1f);
                editText.setLineSpacing(add, mult);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveNote();
                return true;
            case android.R.id.home:
                checkUnsaved(this);
                return true;
            case R.id.action_set_color:
                setNoteColor();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String text = editText.getText().toString();

        if (!TextUtils.isEmpty(text)) {
            Note note = new Note(mNoteId, "", text, Utils.formatColorToHex(selectedColor));
            noteId = db.insertNote(note);
            finish();
        } else {
            editText.setError(getString(R.string.this_cannot_be_empty));
        }
    }

    public void checkUnsaved(final Context context) {
        String text = editText.getText().toString();

        if (!TextUtils.isEmpty(text) && !initialText.equals(text)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.discard_changes);

            builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        checkUnsaved(this);
    }

    private void setNoteColor() {

        int[] colors = Utils.colorChoices(this);
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker_default_title, colors, selectedColor, 4, ColorPickerDialog.SIZE_SMALL);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                selectedColor = color;
                linearLayout.setBackgroundColor(selectedColor);
                Log.d(TAG, "Thi is the color " + color);
            }
        });
        colorPickerDialog.show(getFragmentManager(), "set note color");
    }
}
