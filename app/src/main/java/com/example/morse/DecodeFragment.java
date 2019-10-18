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

import static com.example.morse.ConvertActivity.MORSE_TO_TEXT;

public class DecodeFragment extends Fragment {
    EditText inputText;
    TextView result;
    Button convertToText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decode, container, false);

        inputText = view.findViewById(R.id.phrase_input_decode);
        result = view.findViewById(R.id.result);
        convertToText = view.findViewById(R.id.toTextButton);

        Intent intent = getActivity().getIntent();
        String str = intent.getStringExtra("messageToDecode");

        inputText.setText(str, TextView.BufferType.EDITABLE);

        convertToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String morseString = inputText.getText().toString();

                StringBuilder builder = new StringBuilder();

                if(morseString.contains(".") || morseString.contains("-")) {
                    String[] words = morseString.trim().split("   ");

                    for (String word : words) {
                        for (String morseCharResult : word.split(" ")) {
                            String litera = MORSE_TO_TEXT.get(morseCharResult);
                            builder.append(litera);
                        }

                        builder.append(" ");
                    }
                } else {
                    builder.append("Not a valid morse code");
                }

//                StringBuilder builder = new StringBuilder();
//                String[] words = morseString.trim().split("   ");
//
//                for (String word : words) {
//                    for (String morseCharResult : word.split(" ")) {
//                        String litera = MORSE_TO_TEXT.get(morseCharResult);
//                        builder.append(litera);
//                    }
//
//                    builder.append(" ");
//                }

                result.setText("Result: " + builder.toString().trim());
            }
        });

        return view;
    }
}
