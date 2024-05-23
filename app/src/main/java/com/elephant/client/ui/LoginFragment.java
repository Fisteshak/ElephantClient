package com.elephant.client.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.elephant.client.R;
import com.elephant.client.databinding.FragmentLoginBinding;
import com.elephant.client.models.User;
import com.elephant.client.network.Network;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentLoginBinding binding;
    Network network;
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        binding = FragmentLoginBinding.bind(view);

        Button loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameED = view.findViewById(R.id.enterUsernameEditText);
                EditText passwordED = view.findViewById(R.id.enterPasswordEditText);
                TextView statusLine = view.findViewById(R.id.statusLine);
                String username = usernameED.getText().toString();
                String password = passwordED.getText().toString();

                if (username.isEmpty()) {
                    statusLine.setText("Please enter a username");
                    return;
                }

                if (password.isEmpty()) {
                    statusLine.setText("Please enter a password");
                    return;
                }

                binding.statusLine.setText("Connecting...");

                network = Network.getInstance(new User(username, password));

                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        if (msg.what == Network.RESULT_CODE.SUCCESS.ordinal()) {
                            statusLine.setText("Connection successful");

                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("password", password);

                            NavController navController = Navigation.findNavController(v);
                            navController.navigate(R.id.action_loginFragment_to_mainFragment, bundle);

                        } else if (msg.what == Network.RESULT_CODE.BAD_CREDENTIALS.ordinal()) {
                            statusLine.setText("Bad credentials");
                        } else if (msg.what == Network.RESULT_CODE.NETWORK_FAILURE.ordinal()) {
                            statusLine.setText("No connection to server");
                        }
                        return false;
                    }
                });

                network.testCredentials(handler);
            }
        });




        return view;

    }
}