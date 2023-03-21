package com.acltabontabon.isalin;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@Import(IsalinAutoConfiguration.class)
@SpringBootTest(classes = Application.class)
@EnabledIf(expression = "#{environment['run.itest'] == 'true'}", loadContext = true)
public abstract class IsalinTestBase {

}