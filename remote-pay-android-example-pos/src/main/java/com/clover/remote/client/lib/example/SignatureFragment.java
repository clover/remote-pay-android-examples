/*
 * Copyright (C) 2018 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clover.remote.client.lib.example;

import android.app.Activity;
import androidx.fragment.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import com.clover.common2.Signature2;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.messages.VerifySignatureRequest;


public class SignatureFragment extends DialogFragment {


  private static final String TAG = SignatureFragment.class.getSimpleName();
  private Signature2 signature;
  Button rejectButton;
  Button acceptButton;
  SignatureView sigCanvas;

  private OnFragmentInteractionListener mListener;
  private VerifySignatureRequest verifySignatureRequest;
  private ICloverConnector cloverConnector;

  public static SignatureFragment newInstance(VerifySignatureRequest verifySignatureRequest, ICloverConnector cloverConnector) {
    SignatureFragment fragment = new SignatureFragment();
    fragment.setVerifySignatureRequest(verifySignatureRequest);
    fragment.setCloverConnector(cloverConnector);
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  public SignatureFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_TITLE, R.style.CustomDialog);
  }

  @Override
  public void onStart(){
    super.onStart();
    int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
    int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
    getDialog().getWindow()
        .setLayout(width, height);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View view = inflater.inflate(R.layout.fragment_signature, container, false);

    if (view != null) {
      sigCanvas = (SignatureView) view.findViewById(R.id.SignatureView);
      rejectButton = (Button) view.findViewById(R.id.RejectButton);
      acceptButton = (Button) view.findViewById(R.id.AcceptButton);

      sigCanvas.setSignature(verifySignatureRequest.getSignature());

      acceptButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          Log.d(TAG, "Accepting Signature: " + verifySignatureRequest.toString());
          cloverConnector.acceptSignature(verifySignatureRequest);
        }
      });
      rejectButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          Log.d(TAG, "Rejecting Signature: " + verifySignatureRequest.toString());
          cloverConnector.rejectSignature(verifySignatureRequest);
        }
      });
    }

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();

  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (SignatureFragment.OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
                                   + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public void setVerifySignatureRequest(VerifySignatureRequest verifySignatureRequest) {
    this.verifySignatureRequest = verifySignatureRequest;
    if (sigCanvas != null) {
      sigCanvas.setSignature(verifySignatureRequest.getSignature());
      sigCanvas.postInvalidate();
    }
  }

  public VerifySignatureRequest getVerifySignatureRequest() {
    return verifySignatureRequest;
  }

  public void setCloverConnector(ICloverConnector cloverConnector) {
    this.cloverConnector = cloverConnector;
  }

  public interface OnFragmentInteractionListener {
    public void onFragmentInteraction(Uri uri);
  }


  public void setSignature(Signature2 signature) {
    this.signature = signature;

    int sigWidth = Math.abs(signature.getBounds().first.x - signature.getBounds().second.x);
    int sigHeight = Math.abs(signature.getBounds().first.y - signature.getBounds().second.y);

    sigCanvas.setMinimumHeight(sigHeight);
    sigCanvas.setMinimumWidth(sigWidth);

  }
}
