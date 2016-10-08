package app.sms.com.smslooper;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;

public class MainFragment extends Fragment implements View.OnClickListener , TextWatcher , Title{

    private final String TAB_NAME = "MAIN";
    private Button startButton;
    private Button stopButton;
    private TextView progressBar;
    private EditText numberField;
    private ImageButton contactPicker;
    private EditText messageNumberField;
    private EditText delayfield;
    private EditText messageField;
    private CheckBox decBox;
    private Thread sendingThread;
    private TextView messageLength;
    private boolean threadInterrupted;

    // TODO Add message length counter // DONE
    // TODO Number pickup // DONE
    
    @Override
    public View onCreateView(LayoutInflater inflater,
                                ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.activity_main, container, false);

        startButton = (Button)rootView.findViewById(R.id.startButton);
        stopButton = (Button)rootView.findViewById(R.id.stopButton);
        contactPicker = (ImageButton)rootView.findViewById(R.id.contactPicker);
        progressBar = (TextView)rootView.findViewById(R.id.progressBar);
        numberField = (EditText)rootView.findViewById(R.id.numberField);
        messageNumberField = (EditText)rootView.findViewById(R.id.messageNumberField);
        delayfield = (EditText)rootView.findViewById(R.id.delayField);
        messageField = (EditText)rootView.findViewById(R.id.messageField);
        decBox = (CheckBox)rootView.findViewById(R.id.decBox);
        messageLength = (TextView)rootView.findViewById(R.id.messageLength);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        contactPicker.setOnClickListener(this);
        messageField.addTextChangedListener(this);

        return rootView;
    }

    private void start()
    {
        threadInterrupted = false;
        startButton.setEnabled(false);

        if(!check()){
            startButton.setEnabled(true);
            return;
        }
        stopButton.setEnabled(true);

        sendingThread = new Thread(){
           @Override
           public void run() {
               initAndSendMessage();
               resetButton();
           }
       };

        sendingThread.start();


    }

    public String checkForZerosInNumber(String number)
    {
        String s = "";
        int n = 0;

        while(number.charAt(n) == '0')
        {
            s += "0";
            n++;
        }

        return s;
    }
    //TODO Add + not check // DONE
    private boolean isJustDigit(String text)
    {
        String regex = "\\d+";

        if(text.charAt(0) == '+')
            text = text.substring(1 , text.length());


        if(text.matches(regex))
            return true;

            return false;

    }

    public void resetButton()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
    }

    public void initAndSendMessage()
    {
        String number = numberField.getText().toString().replaceAll("\\s+","");

        if(!isJustDigit(number))
        {
            showMessage("Wrong number" , Toast.LENGTH_SHORT);
            return;
        }


        String zeroInNumber;
        String message = messageField.getText().toString();
        final int numberOfMessageToSend = Integer.parseInt(messageNumberField.getText().toString());
        int delayTime = Integer.parseInt(delayfield.getText().toString());
        BigInteger recipientNumber = new BigInteger(number);

        Log.i("numberOfMessageToSend" , "" + numberOfMessageToSend);

        zeroInNumber = checkForZerosInNumber(number);

        for(int i = 0; i < numberOfMessageToSend; i++)
        {
            if(threadInterrupted){
                showMessage("Thread Interrupted" , Toast.LENGTH_LONG);
                break;
            }

            final int j = i;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setText(j+1 + "/" + numberOfMessageToSend);
                }
            });

            new Message(zeroInNumber + recipientNumber.toString() , message , getContext()).send();

            if(decBox.isChecked())
                recipientNumber = recipientNumber.subtract(new BigInteger("1"));

            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void stop()
    {
        stopButton.setEnabled(false);
        threadInterrupted = true;

    }

    private boolean check()
    {
        if(numberField.getText().length() == 0)
        {
            showMessage("Please enter number", Toast.LENGTH_LONG);
            return false;
        }
        if(messageNumberField.getText().length() == 0 || messageNumberField.getText().toString().equals("0"))
        {
            showMessage("Please enter message number", Toast.LENGTH_LONG);
            return false;
        }
        if(delayfield.getText().length() == 0)
        {
            showMessage("Please enter delay time", Toast.LENGTH_LONG);
            return false;
        }
        if(messageField.getText().length() == 0)
        {
            showMessage("Message can't be empty" , Toast.LENGTH_LONG);
            return false;
        }

        return true;
    }

    private void showMessage(final String msg , final int time)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext() , msg , time).show();
            }
        });

    }

    private void contactPick()
    {
        
        Intent intent = new Intent(Intent.ACTION_PICK , ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent , 1);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.startButton:
                start();
                break;
            case R.id.stopButton:
                stop();
                break;
            case R.id.contactPicker:
                contactPick();
                break;
        }

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    // TODO Add - Show dialog with information when the message is short

    boolean isSpecialCharInMessage = true;

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        int messageLengthNumber = s.length();
        int messagePartLength;

        if(StringUtils.isPureAscii(s.toString()))
        {
            if(!isSpecialCharInMessage)
                Toast.makeText(getContext() , "160 Char Message" , Toast.LENGTH_SHORT).show();

            messagePartLength = (messageLengthNumber / 160) + 1;
            messageLength.setText(messagePartLength + "/" + messageLengthNumber % 160);

            isSpecialCharInMessage = true;
        }
        else {
            if(isSpecialCharInMessage)
                Toast.makeText(getContext() , "70 Char Message" , Toast.LENGTH_SHORT).show();

            messagePartLength = (messageLengthNumber / 70) + 1;
            messageLength.setText(messagePartLength + "/" + messageLengthNumber % 70);

            isSpecialCharInMessage = false;
        }


    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public String getTitle() {
        return TAB_NAME;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != -1)
            return;

        String phoneNum;
        Uri uri = data.getData();

        Cursor cur = getActivity().getContentResolver().query(uri , null , null ,null ,null);

        if(cur.moveToFirst())
        {
            int phoneIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNum = cur.getString(phoneIndex);
            numberField.setText(phoneNum);
        }

        cur.close();

    }
}
