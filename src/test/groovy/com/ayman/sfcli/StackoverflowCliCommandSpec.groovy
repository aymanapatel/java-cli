package com.ayman.sfcli

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.io.ByteArrayOutputStream
import java.io.PrintStream

class StackoverflowCliCommandSpec extends Specification {

    @Shared
    @AutoCleanup
    ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)

    void "test stackoverflow-cli with command line option"() {
        given:
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        System.setOut(new PrintStream(baos))

        String[] args = ['-v'] as String[]
        PicocliRunner.run(StackoverflowCliCommand, ctx, args)

        expect:
        baos.toString().contains('Hi!')
    }

    void "test stackoverflow-cli search command"() {
        given:
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream out = System.out
        System.setOut(new PrintStream(baos))

        // `search -q(question) (question_value) -t(tag) (tag_value) --verbose`
        String[] args = ["search", "-q", "merge maps", "-t", "java", "--verbose"] as String[]
        PicocliRunner.run(StackoverflowCliCommand, ctx, args)
        out.println baos.toString()

        // FORMAT:
        // ✔ 9|3 Merge maps in Groovy
        //       https://stackoverflow.com/questions/213123/merge-maps-groovy
        expect:
        baos.toString() =~ $/✔? \d+\|\d+ [^\n]+\n {6}https://stackoverflow.com/questions/\d[a-z0-9\-]+/$;
    }

}