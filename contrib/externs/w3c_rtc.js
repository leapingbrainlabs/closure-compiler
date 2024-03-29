/*
 * Copyright 2012 Closure Compiler Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @fileoverview Definitions for components of the WebRTC browser API.
 * @see http://dev.w3.org/2011/webrtc/editor/webrtc.html
 * @see http://tools.ietf.org/html/draft-ietf-rtcweb-jsep-01
 * @see http://www.w3.org/TR/mediacapture-streams/
 *
 * @externs
 * @author bemasc@google.com (Benjamin M. Schwartz)
 */

/**
 * @typedef {string}
 * @see {http://dev.w3.org/2011/webrtc/editor/getusermedia.html
 *     #idl-def-MediaStreamTrackState}
 * In WebIDL this is an enum with values 'live', 'mute', and 'ended',
 * but there is no mechanism in Closure for describing a specialization of
 * the string type.
 */
var MediaStreamTrackState;

/**
 * @interface
 * @see http://www.w3.org/TR/mediacapture-streams/#mediastreamtrack
 */
function MediaStreamTrack() {}

/**
 * @type {string}
 * @const
 */
MediaStreamTrack.prototype.kind;

/**
 * @type {string}
 * @const
 */
MediaStreamTrack.prototype.id;

/**
 * @type {string}
 * @const
 */
MediaStreamTrack.prototype.label;

/**
 * @type {boolean}
 */
MediaStreamTrack.prototype.enabled;

/**
 * TODO(bemasc): Remove this attribute once browsers are updated
 * @deprecated
 * The number 0.
 * @type {number}
 * @const
 */
MediaStreamTrack.prototype.LIVE;

/**
 * TODO(bemasc): Remove this attribute once browsers are updated
 * @deprecated
 * The number 1.
 * @type {number}
 * @const
 */
MediaStreamTrack.prototype.MUTED;

/**
 * TODO(bemasc): Remove this attribute once browsers are updated
 * @deprecated
 * The number 2.
 * @type {number}
 * @const
 */
MediaStreamTrack.prototype.ENDED;

/**
 * TODO(bemasc): Remove "number" once browsers are updated.
 * @type {number|MediaStreamTrackState}
 * Read only.
 */
MediaStreamTrack.prototype.readyState;

/**
 * @type {?function(!Event)}
 */
MediaStreamTrack.prototype.onmute;

/**
 * @type {?function(!Event)}
 */
MediaStreamTrack.prototype.onunmute;

/**
 * @type {?function(!Event)}
 */
MediaStreamTrack.prototype.onended;

/**
 * @constructor
 * @extends {Event}
 * @private
 * @see http://dev.w3.org/2011/webrtc/editor/
 * webrtc-20120720.html#mediastreamtrackevent
 * TODO(bemasc): Update this link to the final definition once one exists
 * (https://www.w3.org/Bugs/Public/show_bug.cgi?id=19568)
 */
function MediaStreamTrackEvent() {}

/**
 * @type {!MediaStreamTrack}
 * @const
 */
MediaStreamTrackEvent.prototype.track;

/**
 * TODO(bemasc): Remove this type once implementations are updated to the
 * new API.
 * 
 * @deprecated
 * @interface
 * @see http://www.w3.org/TR/mediacapture-streams/#mediastreamtracklist
 */
function MediaStreamTrackList() {}

/**
 * @type {number}
 */
MediaStreamTrackList.prototype.length;

/**
 * @param {number} index
 * @return {?MediaStreamTrack}
 * TODO(bemasc): Get spec clarification: can item() return null for an invalid
 * index?
 * @nosideeffects
 */
MediaStreamTrackList.prototype.item = function(index) {};

/**
 * @param {!MediaStreamTrack} track
 */
MediaStreamTrackList.prototype.add = function(track) {};

/**
 * @param {!MediaStreamTrack} track
 */
MediaStreamTrackList.prototype.remove = function(track) {};

/**
 * @type {?function(!MediaStreamTrackEvent)}
 */
MediaStreamTrackList.prototype.onaddtrack;

/**
 * @type {?function(!MediaStreamTrackEvent)}
 */
MediaStreamTrackList.prototype.onremovetrack;

/**
 * @param {MediaStream|Array.<!MediaStreamTrack>=} streamOrTracks
 * @constructor
 * @implements {EventTarget}
 * @see http://www.w3.org/TR/mediacapture-streams/#mediastream
 */
function MediaStream(streamOrTracks) {}

/** @override */
MediaStream.prototype.addEventListener = function(type, listener,
    useCapture) {};

/** @override */
MediaStream.prototype.removeEventListener = function(type, listener,
    useCapture) {};

/** @override */
MediaStream.prototype.dispatchEvent = function(evt) {};

/**
 * TODO(bemasc): Remove this property.
 * @deprecated
 * @type {string}
 * @const
 */
MediaStream.prototype.label;

/**
 * @type {string}
 * @const
 */
MediaStream.prototype.id;

/**
 * TODO(bemasc): Remove this property once browsers are updated.
 * @deprecated
 * @type {MediaStreamTrackList}
 * @const
 */
MediaStream.prototype.audioTracks;

/**
 * @return {!Array.<!MediaStreamTrack>}
 */
MediaStream.prototype.getAudioTracks = function() {};

/**
 * TODO(bemasc): Remove this property once browsers are updated.
 * @deprecated
 * @type {MediaStreamTrackList}
 * @const
 */
MediaStream.prototype.videoTracks;

/**
 * @return {!Array.<!MediaStreamTrack>}
 */
MediaStream.prototype.getVideoTracks = function() {};

/**
 * @param {string} trackId
 * @return {MediaStreamTrack}
 */
MediaStream.prototype.getTrackById = function(trackId) {};

/**
 * @param {!MediaStreamTrack} track
 */
MediaStream.prototype.addTrack = function(track) {};

/**
 * @param {!MediaStreamTrack} track
 */
MediaStream.prototype.removeTrack = function(track) {};

/**
 * @type {boolean}
 */
MediaStream.prototype.ended;

/**
 * @type {?function(!Event)}
 */
MediaStream.prototype.onended;

/**
 * @type {?function(!MediaStreamTrackEvent)}
 */
MediaStream.prototype.onaddtrack;

/**
 * @type {?function(!MediaStreamTrackEvent)}
 */
MediaStream.prototype.onremovetrack;

/**
 * @constructor
 * @extends {MediaStream}
 * @private
 * @see http://www.w3.org/TR/mediacapture-streams/#localmediastream
 */
function LocalMediaStream() {}

LocalMediaStream.prototype.stop = function() {};

/**
 * This interface defines the available constraint attributes.  These are the
 * attributes defined in
 * {@see http://tools.ietf.org/html/draft-alvestrand-constraints-resolution-01}.
 * Note that although that draft refers to "Media Constraints", the W3C uses
 * the terms "Media[Stream|Track]Constraints" for this type, and
 * defines a different type (for RTCPeerConnection) called "MediaConstraints".
 *
 * This interface type is not part of any standard, so it is marked as private.
 * It is defined here in order to reserve the property names, which would
 * otherwise be rewritten when the compiler processes an object literal.
 * Several subsequent interfaces are defined in the same pattern.
 *
 * Note that although this list includes all the properties supported by
 * libjingle (and hence by Chromium), browsers are permitted to offer other
 * properties as well ({
 * @see http://tools.ietf.org/html/draft-burnett-rtcweb-constraints-registry-02
 * }), and browsers are expected to silently ignore unknown properties.  This
 * creates the potential for a very confusing situation in which properties
 * not listed here are renamed by the compiler and then ignored by the browser.
 *
 * @interface
 * @private
 */
function MediaTrackConstraintSetInterface_() {}

/**
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.minWidth;

/**
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.maxWidth;

/**
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.minHeight;

/**
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.maxHeight;

/**
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.minAspectRatio;

/**
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.maxAspectRatio;

/**
 * Due to a typo, this is called "minFramerate" in the -01 draft.
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.minFrameRate;

/**
 * @type {?number}
 */
MediaTrackConstraintSetInterface_.prototype.maxFrameRate;

/**
 * This type and two more below are defined as unions with Object because they
 * are normally used as record types by constructing an Object literal, but all
 * of their properties are optional.
 * @typedef {Object|MediaTrackConstraintSetInterface_}
 */
var MediaTrackConstraintSet;

/**
 * @interface
 * @private
 */
function MediaTrackConstraintsInterface_() {}

/**
 * @type {?MediaTrackConstraintSet}
 */
MediaTrackConstraintsInterface_.prototype.mandatory;

/**
 * @type {?Array.<!MediaTrackConstraintSet>}
 */
MediaTrackConstraintsInterface_.prototype.optional;

/**
 * @typedef {Object|MediaTrackConstraintsInterface_}
 */
var MediaTrackConstraints;

/**
 * @interface
 * @private
 */
function MediaStreamConstraintsInterface_() {}

/**
 * @type {boolean|MediaTrackConstraints}
 */
MediaStreamConstraintsInterface_.prototype.audio;

/**
 * @type {boolean|MediaTrackConstraints}
 */
MediaStreamConstraintsInterface_.prototype.video;

/**
 * @typedef {Object|MediaStreamConstraintsInterface_}
 */
var MediaStreamConstraints;

/**
 * @interface
 */
function NavigatorUserMediaError() {}

/**
 * @type {number}
 * @const
 */
NavigatorUserMediaError.prototype.PERMISSION_DENIED;  /** 1 */

/**
 * @type {number}
 * Read only.
 */
NavigatorUserMediaError.prototype.code;

/**
 * @param {MediaStreamConstraints} constraints A MediaStreamConstraints object.
 * @param {function(!LocalMediaStream)} successCallback
 *     A NavigatorUserMediaSuccessCallback function.
 * @param {function(!NavigatorUserMediaError)=} errorCallback A
 *     NavigatorUserMediaErrorCallback function.
 * @see http://dev.w3.org/2011/webrtc/editor/getusermedia.html
 * @see http://www.w3.org/TR/mediacapture-streams/
 */
Navigator.prototype.webkitGetUserMedia =
  function(constraints, successCallback, errorCallback) {};

/**
 * @param {string} type
 * @param {!Object} eventInitDict
 * @constructor
 */
function MediaStreamEvent(type, eventInitDict) {}

/**
 * @type {?MediaStream}
 * @const
 */
MediaStreamEvent.prototype.stream;

/**
 * @typedef {string}
 * @see http://www.w3.org/TR/webrtc/#rtcsdptype
 * In WebIDL this is an enum with values 'offer', 'pranswer', and 'answer',
 * but there is no mechanism in Closure for describing a specialization of
 * the string type.
 */
var RTCSdpType;

/**
 * @param {!Object=} descriptionInitDict The RTCSessionDescriptionInit
 * dictionary.  This optional argument may have type
 * {type:RTCSdpType, sdp:string}, but neither of these keys are required to be
 * present, and other keys are ignored, so the closest Closure type is Object.
 * @constructor
 * @see http://dev.w3.org/2011/webrtc/editor/webrtc.html#rtcsessiondescription-class
 */
function RTCSessionDescription(descriptionInitDict) {}

/**
 * @type {?RTCSdpType}
 * @see http://www.w3.org/TR/webrtc/#widl-RTCSessionDescription-type
 */
RTCSessionDescription.prototype.type;

/**
 * @type {?string}
 * @see http://www.w3.org/TR/webrtc/#widl-RTCSessionDescription-sdp
 */
RTCSessionDescription.prototype.sdp;

/**
 * TODO(bemasc): Remove this definition once it is removed from the browser.
 * @param {string} label The label index (audio/video/data -> 0,1,2)
 * @param {string} sdp The ICE candidate in SDP text form
 * @constructor
 */
function IceCandidate(label, sdp) {}

/**
 * @return {string}
 */
IceCandidate.prototype.toSdp = function() {};

/**
 * @type {?string}
 */
IceCandidate.prototype.label;

/**
 * @param {!Object=} candidateInitDict  The RTCIceCandidateInit dictionary.
 * This optional argument may have type
 * {candidate: string, sdpMid: string, sdpMLineIndex:number}, but none of
 * these keys are required to be present, and other keys are ignored, so the
 * closest Closure type is Object.
 * @constructor
 * @see http://www.w3.org/TR/webrtc/#rtcicecandidate-type
 */
function RTCIceCandidate(candidateInitDict) {}

/**
 * @type {?string}
 */
RTCIceCandidate.prototype.candidate;

/**
 * @type {?string}
 */
RTCIceCandidate.prototype.sdpMid;

/**
 * @type {?number}
 */
RTCIceCandidate.prototype.sdpMLineIndex;

/**
 * @typedef {{url: string}}
 * @private
 * @see http://www.w3.org/TR/webrtc/#rtciceserver-type
 * This dictionary type also has an optional key {credential: ?string}.
 */
var RTCIceServerRecord_;

/**
 * @interface
 * @private
 */
function RTCIceServerInterface_() {}

/**
 * @type {string}
 */
RTCIceServerInterface_.prototype.url;

/**
 * @type {?string}
 */
RTCIceServerInterface_.prototype.credential;

/**
 * This type, and several below it, are constructed as unions between records 
 *
 * @typedef {RTCIceServerRecord_|RTCIceServerInterface_}
 * @private
 */
var RTCIceServer;

/**
 * @typedef {{iceServers: !Array.<!RTCIceServer>}}
 * @private
 */
var RTCConfigurationRecord_;

/**
 * @interface
 * @private
 */
function RTCConfigurationInterface_() {}

/**
 * @type {!Array.<!RTCIceServer>}
 */
RTCConfigurationInterface_.prototype.iceServers;

/**
 * @typedef {RTCConfigurationRecord_|RTCConfigurationInterface_}
 */
var RTCConfiguration;

/**
 * @typedef {function(!RTCSessionDescription)}
 */
var RTCSessionDescriptionCallback;

/**
 * @typedef {function(string)}
 */
var RTCPeerConnectionErrorCallback;

/**
 * @typedef {function()}
 */
var RTCVoidCallback;

/**
 * @typedef {string}
 */
var RTCPeerState;

/**
 * @typedef {string}
 */
var RTCIceState;

/**
 * @typedef {string}
 */
var RTCGatheringState;

/**
 * @param {string} type
 * @param {!Object} eventInitDict
 * @constructor
 */
function RTCPeerConnectionIceEvent(type, eventInitDict) {}

/**
 * @type {RTCIceCandidate}
 * @const
 */
RTCPeerConnectionIceEvent.prototype.candidate;

// These RTCStats types represent Webkit's current implementation, which is
// different from the latest spec.
// TODO(bemasc): Update this once spec and code are reconciled.
/**
 * @interface
 */
function RTCStatsElement() {}

/**
 * @type {Date}
 * @const
 */
RTCStatsElement.prototype.timestamp;

/**
 * @return {!Array.<!string>}
 */
RTCStatsElement.prototype.names = function() {};

/**
 * @param {string} name
 * @return {string}
 */
RTCStatsElement.prototype.stat = function(name) {};

/**
 * @interface
 */
function RTCStatsReport() {}

/**
 * @type {RTCStatsElement}
 * @const
 */
RTCStatsReport.prototype.local;

/**
 * @type {RTCStatsElement}
 * @const
 */
RTCStatsReport.prototype.remote;

/**
 * @interface
 */
function RTCStatsResponse() {}

/**
 * @return {!Array.<!RTCStatsReport>}
 */
RTCStatsResponse.prototype.result = function() {};

/**
 * @typedef {function(!RTCStatsResponse, MediaStreamTrack=)}
 */
var RTCStatsCallback;

/**
 * This type is not yet standardized, so the properties here only represent
 * the current capabilities of libjingle (and hence Chromium).
 * TODO(bemasc): Add a link to the relevant standard once MediaConstraint has a
 * standard definition.
 *
 * @interface
 * @private
 */
function MediaConstraintSetInterface_() {}

/**
 * @type {?boolean}
 */
MediaConstraintSetInterface_.prototype.OfferToReceiveAudio;

/**
 * @type {?boolean}
 */
MediaConstraintSetInterface_.prototype.OfferToReceiveVideo;

/**
 * @type {?boolean}
 */
MediaConstraintSetInterface_.prototype.DtlsSrtpKeyAgreement;

/**
 * @type {?boolean}
 */
MediaConstraintSetInterface_.prototype.RtpDataChannels;

/**
 * TODO(bemasc): Make this type public once it is defined in a standard.
 *
 * @typedef {Object|MediaConstraintSetInterface_}
 * @private
 */
var MediaConstraintSet_;

/**
 * @interface
 * @private
 */
function MediaConstraintsInterface_() {}

/**
 * @type {?MediaConstraintSet_}
 */
MediaConstraintsInterface_.prototype.mandatory;

/**
 * @type {?Array.<!MediaConstraintSet_>}
 */
MediaConstraintsInterface_.prototype.optional;

/**
 * This type is used extensively in
 * {@see http://dev.w3.org/2011/webrtc/editor/webrtc.html} but is not yet
 * defined.
 *
 * @typedef {Object|MediaConstraintsInterface_}
 */
var MediaConstraints;

/**
 * @param {!RTCConfiguration} configuration
 * @param {!MediaConstraints=} constraints
 * @constructor
 * @implements {EventTarget}
 */
function RTCPeerConnection(configuration, constraints) {}

/** @override */
RTCPeerConnection.prototype.addEventListener = function(
    type, listener, useCapture) {};

/** @override */
RTCPeerConnection.prototype.removeEventListener = function(
    type, listener, useCapture) {};

/** @override */
RTCPeerConnection.prototype.dispatchEvent = function(evt) {};

/**
 * @param {!RTCSessionDescriptionCallback} successCallback
 * @param {!RTCPeerConnectionErrorCallback=} failureCallback
 * @param {!MediaConstraints=} constraints
 */
RTCPeerConnection.prototype.createOffer = function(successCallback,
    failureCallback, constraints) {};

/**
 * @param {RTCSessionDescriptionCallback} successCallback
 * @param {?RTCPeerConnectionErrorCallback=} failureCallback
 * @param {!MediaConstraints=} constraints
 */
RTCPeerConnection.prototype.createAnswer = function(successCallback,
    failureCallback, constraints) {};

/**
 * @param {!RTCSessionDescription} description
 * @param {!RTCVoidCallback=} successCallback
 * @param {!RTCPeerConnectionErrorCallback=} failureCallback
 */
RTCPeerConnection.prototype.setLocalDescription = function(description,
    successCallback, failureCallback) {};

/**
 * @param {!RTCSessionDescription} description
 * @param {!RTCVoidCallback=} successCallback
 * @param {!RTCPeerConnectionErrorCallback=} failureCallback
 */
RTCPeerConnection.prototype.setRemoteDescription = function(description,
    successCallback, failureCallback) {};

/**
 * @type {?RTCSessionDescription}
 * Read only.
 */
RTCPeerConnection.prototype.localDescription;

/**
 * @type {?RTCSessionDescription}
 * Read only.
 */
RTCPeerConnection.prototype.remoteDescription;

/**
 * @type {RTCPeerState}
 * Read only.
 */
RTCPeerConnection.prototype.readyState;

/**
 * @param {?RTCConfiguration=} configuration
 * @param {?MediaConstraints=} constraints
 */
RTCPeerConnection.prototype.updateIce = function(configuration, constraints) {};

/**
 * @param {!RTCIceCandidate} candidate
 */
RTCPeerConnection.prototype.addIceCandidate = function(candidate) {};

/**
 * @type {!RTCGatheringState}
 * Read only.
 */
RTCPeerConnection.prototype.iceGatheringState;

/**
 * @type {!RTCIceState}
 * Read only.
 */
RTCPeerConnection.prototype.iceState;

/**
 * TODO(bemasc): Remove this attribute once browsers are updated.
 * @deprecated
 * @type {!Array.<!MediaStream>}
 * Read only.
 */
RTCPeerConnection.prototype.localStreams;

/**
 * @return {!Array.<!MediaStream>}
 */
RTCPeerConnection.prototype.getLocalStreams = function() {};

/**
 * TODO(bemasc): Remove this attribute once browsers are updated.
 * @deprecated
 * @type {!Array.<!MediaStream>}
 * Read only.
 */
RTCPeerConnection.prototype.remoteStreams;

/**
 * @return {!Array.<!MediaStream>}
 */
RTCPeerConnection.prototype.getRemoteStreams = function() {};

/**
 * @param {string} streamId
 * @return {MediaStream}
 */
RTCPeerConnection.prototype.getStreamById = function(streamId) {};

// TODO(bemasc): Add createDataChannel (and DataChannel and related types).

/**
 * @param {!MediaStream} stream
 * @param {!MediaConstraints=} constraints
 */
RTCPeerConnection.prototype.addStream = function(stream, constraints) {};

/**
 * @param {!MediaStream} stream
 */
RTCPeerConnection.prototype.removeStream = function(stream) {};

// TODO(bemasc): Add identity provider stuff once implementations exist

/**
 * @param {!RTCStatsCallback} successCallback
 * @param {MediaStreamTrack=} selector
 */
RTCPeerConnection.prototype.getStats = function(successCallback, selector) {};

RTCPeerConnection.prototype.close = function() {};

/**
 * @type {?function(!Event)}
 */
RTCPeerConnection.prototype.onnegotiationneeded;

/**
 * @type {?function(!RTCPeerConnectionIceEvent)}
 */
RTCPeerConnection.prototype.onicecandidate;

/**
 * @type {?function(!Event)}
 */
RTCPeerConnection.prototype.onopen;

/**
 * @type {?function(!Event)}
 */
RTCPeerConnection.prototype.onstatechange;

/**
 * @type {?function(!MediaStreamEvent)}
 */
RTCPeerConnection.prototype.onaddstream;

/**
 * @type {?function(!MediaStreamEvent)}
 */
RTCPeerConnection.prototype.onremovestream;

/**
 * @type {?function(!Event)}
 */
RTCPeerConnection.prototype.ongatheringchange;

/**
 * @type {?function(!Event)}
 */
RTCPeerConnection.prototype.onicechange;

/**
 * @type {function(new: RTCPeerConnection, !RTCConfiguration,
 *     !MediaConstraints=)}
 */
var webkitRTCPeerConnection;
