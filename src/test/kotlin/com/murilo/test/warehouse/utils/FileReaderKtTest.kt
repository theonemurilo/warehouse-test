package com.murilo.test.warehouse.utils

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import java.nio.charset.StandardCharsets.UTF_8

internal class FileReaderKtTest {

    @Test
    fun `should read the file and convert it to a Mono of String`() {
        val filePayload = "dummy text"
        val dataBuffer = mockk<DataBuffer> {
            every { asInputStream() } returns filePayload.byteInputStream(UTF_8)
        }
        val filePart = mockk<FilePart>(relaxed = true) {
            every { content() } returns Flux.just(dataBuffer)
        }

        val content = readFile(Flux.just(filePart)).block()

        content shouldBe filePayload
    }
}