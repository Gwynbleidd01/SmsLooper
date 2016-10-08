package app.sms.com.smslooper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class LogWindow extends Fragment implements View.OnClickListener, Title{

    public static LogWindow logWindow;
    private EditText log;
    private Button clearLogBtn;
    private final String TAB_NAME = "LOG";
    private Activity activity;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        logWindow = this;
        activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.logwindow_layout , container , false);
        clearLogBtn = (Button)rootView.findViewById(R.id.clearLog);
        log = (EditText)rootView.findViewById(R.id.log);

        clearLogBtn.setOnClickListener(this);

        return rootView;
    }

    private void appendText(final String text , final int color) {

        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String date = new TimeClass().getCurrentDateAndTime();
                Spannable WordToSpan = new SpannableString(text);
                WordToSpan.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                log.append(date + ": ");
                log.append(WordToSpan);
                log.append("\n");
            }
        });
    }

    public void error(String text , boolean fileLogging)
    {
        appendText(text , Color.RED);
        if (fileLogging)
            File.FileWriter(text , File.DEFAULT_PATH + "/log.txt");
    }

    public void info(String text , boolean fileLogging)
    {
        appendText(text , Color.BLUE);
        if (fileLogging)
            File.FileWriter(text , File.DEFAULT_PATH + "/log.txt");
    }

    public void warning(String text , boolean fileLogging)
    {
        appendText(text , Color.GRAY);
        if (fileLogging)
            File.FileWriter(text , File.DEFAULT_PATH + "/log.txt");
    }


    @Override
    public String getTitle() {
        return TAB_NAME;
    }

    @Override
    public void onClick(View v) {
        log.setText("");
    }
}

