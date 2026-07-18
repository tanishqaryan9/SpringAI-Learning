package com.SpringAI.Generator;

import com.deepgram.DeepgramClient;
import com.deepgram.resources.listen.v1.media.types.MediaTranscribeResponse;
import com.deepgram.resources.speak.v1.audio.requests.SpeakV1Request;
import com.deepgram.types.ListenV1AcceptedResponse;
import com.deepgram.types.ListenV1Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AudioService {

    private final DeepgramClient client;

    public String transcribe(MultipartFile file) throws Exception
    {
        byte[] audioData= file.getBytes();

        MediaTranscribeResponse response=client.listen().v1().media().transcribeFile(audioData);

        StringBuilder transcript=new StringBuilder();

        response.visit(new MediaTranscribeResponse.Visitor<Void>() {
            @Override
            public Void visit(ListenV1Response result) {
                result.getResults().getChannels().get(0).getAlternatives().ifPresent(alt->{
                    alt.get(0).getTranscript().ifPresent(transcript::append);
                });
                return null;
            }

            @Override
            public Void visit(ListenV1AcceptedResponse accepted) {
                transcript.append("Request accepted for processing");
                return null;
            }
        });
        return transcript.toString();
    }

    public InputStream textToSpeech(String text) {


        return client.speak()
                .v1()
                .audio()
                .generate(
                        SpeakV1Request.builder()
                                .text(text)
                                .build()
                );
    }
}