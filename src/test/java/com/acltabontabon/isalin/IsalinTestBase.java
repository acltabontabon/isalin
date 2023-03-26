package com.acltabontabon.isalin;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(IsalinAutoConfiguration.class)
@SpringBootTest(classes = Application.class)
public abstract class IsalinTestBase {

}