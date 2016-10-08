package app.sms.com.smslooper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;


public class Message {

    private String message;
    private String recipient;
    private SmsManager smsManager;
    private static int uniqueID;
    private Context con;
    private final static String broadcastID = "SENT";

    private static BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equalsIgnoreCase(broadcastID))
            {
                Log.i("ResultCode" , "" + getResultCode());

                String phoneNumber = intent.getStringExtra("phoneNumber");
                Message.appendToLogText(getResultCode() , phoneNumber);
               // con.unregisterReceiver(this);
            }
        }
    };

    private static void appendToLogText(int resultCode , String phonenumber)
    {
        switch (resultCode) {
            case Activity.RESULT_OK:
                LogWindow.logWindow.info(phonenumber + " - Sent" , true);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                LogWindow.logWindow.error(phonenumber + " - Error - Generic failure" , true);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                LogWindow.logWindow.error(phonenumber + " - Error - No Service" , true);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                LogWindow.logWindow.error(phonenumber + " - Error - Null PDU" , true);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                LogWindow.logWindow.error(phonenumber + " - Error - Radio off" , true);
                break;
            default:
                LogWindow.logWindow.warning(phonenumber + " Result Code " + resultCode , true);
        }
    }

    public Message(String  recipient, String message , Context con)
    {
        this.message = message;
        this.recipient = recipient;
        this.con = con;
        smsManager = SmsManager.getDefault();

    }

    // TODO Add MultiPart Message Send // DONE // TODO Add broadcast receiver
    // TODO Add write to file all sending number // DONE
    // TODO Short length with special char in message // DONE but change the encoding to GSM
    // TODO Log turn off
    // TODO Fix the loosing 0 in number // DONE
    // TODO Add ads
    // TODO Add sending message in service
    private void sendOnePartMessage() throws Exception{

        Intent intent = new Intent(broadcastID);
        intent.putExtra("phoneNumber" , recipient);
        PendingIntent sentPI = PendingIntent.getBroadcast(con, uniqueID++ , intent , PendingIntent.FLAG_CANCEL_CURRENT);
        IntentFilter sendPIFilter = new IntentFilter(broadcastID);

        smsManager.sendTextMessage(recipient, null, message, sentPI, null);

        con.registerReceiver(sentReceiver , sendPIFilter);
    }

    private void sendMultiPartMessage() throws Exception{

        ArrayList<String> parts = smsManager.divideMessage(message);
        smsManager.sendMultipartTextMessage(recipient, null, parts, null, null);

    }

    void send()
    {
        int messageMaxLength = StringUtils.isPureAscii(message) ? 160 : 70;

        Log.i("Recipient + message" , recipient + " " + message);

        try {

            if ((message.length() / messageMaxLength) >= 1) {
                sendMultiPartMessage();
                LogWindow.logWindow.info(recipient + " - Sent" , true);
            }
            else
                sendOnePartMessage();

        } catch (Exception e) {
            LogWindow.logWindow.error("Send Error to " + recipient , true);
        }

    }
}
