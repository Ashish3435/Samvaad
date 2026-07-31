package com.ashish.samvaad.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallSignalRequest {

    private String roomCode;

    private String type;
    // type: "call-invite" | "call-accept" | "call-reject" | "call-end"
    //     | "webrtc-offer" | "webrtc-answer" | "webrtc-ice-candidate"

    private String targetEmail;
    // for direct peer-to-peer signaling messages (offer/answer/ice/accept/reject) —
    // who this specific message is meant for. Null for broadcast messages (invite/end).

    private String callType;
    // "audio" | "video" — only meaningful on "call-invite"

    private Object payload;
    // holds the actual SDP offer/answer or ICE candidate object, passed through as-is
}