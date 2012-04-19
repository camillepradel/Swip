/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swip.workflow.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
//@XmlRootElement(name = "matching")
public class ResponseMatching {

    String keyWord = null;
    String matchingType = null;
    String uri = null;
    String matchingQuerried = null;
    String matchingMatchedLabel = null;
    String matchingTrustMark = "0";

    public ResponseMatching(String keyWord,  String matchingType, String uri, String matchingQuerried, String matchingMatchedLabel, String matchingTrustMark) {
        this.keyWord = keyWord;
        this.uri = uri;
        this.matchingType = matchingType;
        this.matchingQuerried = matchingQuerried;
        this.matchingMatchedLabel = matchingMatchedLabel;
        this.matchingTrustMark = matchingTrustMark;
    }

    public ResponseMatching() {
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getmatchingType() {
        return matchingType;
    }

    public void setmatchingType(String matchingType) {
        this.matchingType = matchingType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getmatchingQuerried() {
        return matchingQuerried;
    }

    public void setmatchingQuerried(String matchingQuerried) {
        this.matchingQuerried = matchingQuerried;
    }

    public String getmatchingMatchedLabel() {
        return matchingMatchedLabel;
    }

    public void setmatchingMatchedLabel(String matchingMatchedLabel) {
        this.matchingMatchedLabel = matchingMatchedLabel;
    }

    public String getmatchingTrustMark() {
        return matchingTrustMark;
    }

    public void setmatchingTrustMark(String matchingTrustMark) {
        this.matchingTrustMark = matchingTrustMark;
    }
}
