# QuirkQuestLab

### Requirements:

* Maven -  min. 3.8
* Java 17
* Allure -> For reports https://allurereport.org/docs/gettingstarted-installation/

Run tests with ignoring failed option.
```shell
mvn clean install -Dmaven.test.failure.ignore=true
```
Generate report for tests.
```shell
allure generate target/allure-results/ -o allure/reports --clean
```

### Reports
1. Open [index.html](allure%2Freports%2Findex.html) in your browser.
2. Switch to Behaviours.
3. You will see tests divided by API and UI.