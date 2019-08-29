FROM docker:18.05.0-ce

# Create jenkins user
RUN adduser -S -u 10000 jenkins && \
    addgroup jenkins root && \
    addgroup jenkins bin && \
    addgroup jenkins daemon && \
    addgroup jenkins sys && \
    addgroup jenkins adm && \
    addgroup jenkins disk && \
    addgroup jenkins wheel && \
    addgroup jenkins floppy && \
    addgroup jenkins dialout && \
    addgroup jenkins tape && \
    addgroup jenkins video

USER jenkins
