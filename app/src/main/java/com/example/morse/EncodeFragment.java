package com.example.morse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.morse.ConvertActivity.TEXT_TO_MORSE;

public class EncodeFragment extends Fragment {
//    static final String TAG = "Encode Fragment";

    EditText inputText;
    TextView result;
    TextView currentChar;
    Button convertToMorse;
    Button sendMorse;

    String litera;
    String morseCharResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encode, container, false);

        inputText = view.findViewById(R.id.phrase_input_encode);
        result = view.findViewById(R.id.result);
        convertToMorse = view.findViewById(R.id.toMorseButton);
        currentChar = view.findViewById(R.id.current_char);
        sendMorse = view.findViewById(R.id.send_morse_sms);

        sendMorse.setEnabled(false);

        sendMorse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultMorseSent = result.getText().toString().substring(8);

                Intent intent = new Intent(getActivity().getApplicationContext(), SmsActivity.class);
                intent.putExtra("encodedMessage", resultMorseSent);

                startActivity(intent);
            }
        });

        convertToMorse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String textString = inputText.getText().toString();

                            StringBuilder builder = new StringBuilder();
                            String[] words = textString.trim().split(" ");

                            for (String word : words) {
                                for (int i = 0; i < word.length(); i++) {
                                    litera = word.substring(i, i + 1).toLowerCase();
                                    morseCharResult = TEXT_TO_MORSE.get(litera);
                                    builder.append(morseCharResult).append(" ");
                                }
                                builder.append("  ");
                            }

                            String res = builder.toString().trim();

//                            Pattern p = Pattern.compile("[a-z0-9!,?.]");
//                            Matcher m = p.matcher(res);

                            if(Objects.equals("", builder.toString().trim())) {
                                result.setText("Result: Please enter only letters");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMorse.setEnabled(false);
                                        currentChar.setText("Current char: None");
                                    }
                                });
                            } else {
                                result.setText("Result: " + res);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMorse.setEnabled(true);
                                    }
                                });
                            }

                            for (String word : words) {
                                for (int i = 0; i < word.length(); i++) {
                                    litera = word.substring(i, i + 1).toLowerCase();
                                    morseCharResult = TEXT_TO_MORSE.get(litera);
                                    //builder.append(morseCharResult).append(" ");

                                    getActivity().runOnUiThread(new Runnable() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void run() {
                                            currentChar.setText("Current char: " + litera + " ( " + morseCharResult + " )");
                                        }
                                    });

                                    for (char c : morseCharResult.toCharArray()) {
                                        ((ConvertActivity)getActivity()).flashLightOn();

                                        if (c == '.') {
                                            Thread.sleep((long) (0.3 * 1 * 1000));
                                        }
                                        if (c == '-') {
                                            Thread.sleep((long) (0.3 * 3 * 1000));
                                        }

                                        ((ConvertActivity)getActivity()).flashLightOff();

                                        Thread.sleep((long) (0.3 * 1 * 1000));
                                    }
                                    Thread.sleep((long) (0.3 * 3 * 1000));
                                }
                                Thread.sleep((long) (0.3 * 7 * 1000));

                                //builder.append("  ");
                            }

                            //result.setText(builder.toString());
                        } catch(InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return view;
    }
}
