package com.kylemsguy.tcasmobile.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class QAObject {
    private int id;
    private String content;

    public QAObject(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return "QAObject <" + id + "> " + content;
    }

    public static List<Question> parseData(String data)
            throws NoSuchQuestionException {
        List<Question> questions = new ArrayList<Question>();

        // regex objects
        Pattern qPattern = Pattern
                .compile("lsQ\\^i(\\d+)\\^b[01]\\^i1\\^s(.*?)\\^\\^");
        Pattern aPattern = Pattern
                .compile("lsA\\^i(\\d+)\\^i(\\d+)\\^b([01])\\^s(.*?)\\^\\^");

        Matcher qMatcher = qPattern.matcher(data);
        Matcher aMatcher = aPattern.matcher(data);

        while (qMatcher.find()) {
            // get data from regex
            int id = Integer.parseInt(qMatcher.group(1));
            String strQ = qMatcher.group(2).replaceAll("\\$n", "\n");
            Question q = new Question(id, strQ);

            // insert into map
            questions.add(q);
        }

        while (aMatcher.find()) {
            // get data from regex
            int qId = Integer.parseInt(aMatcher.group(2));
            int aId = Integer.parseInt(aMatcher.group(1));
            int intRead = Integer.parseInt(aMatcher.group(3));
            boolean read;
            // check if read
            if (intRead == 0) {
                read = false;
            } else {
                read = true;
            }
            String ans = aMatcher.group(4).replaceAll("\\$n", "\n");

            // get relevant Question object
            Question q = null;

            for (Question iq : questions) {
                if (iq.getId() == qId) {
                    q = iq;
                    break;
                }
            }

            if (q == null) {
                throw new NoSuchQuestionException(
                        "Can't find question that was just inserted. Something has gone horribly wrong.");
            }

            // create answer object
            Answer a = new Answer(aId, ans, q, read);

            // add to answer object
            q.addAnswer(a);
        }

        return questions;
    }

    public static Map<String, List<String>> questionToListData(List<Question> questions) {
        Map<String, List<String>> listData = new TreeMap<String, List<String>>();

        for (Question q : questions) {
            String questionTitle = q.getContent();
            List<String> answerTitles = new ArrayList<String>();

            for (Answer a : q.getAnswers()) {
                answerTitles.add(a.getContent());
            }

            listData.put(questionTitle, answerTitles);

        }
        return listData;

    }

}
