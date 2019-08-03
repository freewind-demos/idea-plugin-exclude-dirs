package freewind.excludedirs

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.FileSystems
import java.nio.file.Paths

object RuleSpec : Spek({
    describe("xxx") {
        it("yyy") {
            println("###########################")
            val pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/aaa")
            assertThat(pathMatcher.matches(Paths.get("/aaa"))).isTrue()
            assertThat(pathMatcher.matches(Paths.get("/111/aaa"))).isTrue()
        }
    }
    describe("partial path rules") {
        val rule = Rule("node_modules")
        describe("match") {
            //            it("should match file paths include the rule") {
//                assertThat(rule.matches("node_modules")).isTrue()
//                assertThat(rule.matches("/node_modules/")).isTrue()
//                assertThat(rule.matches("aaa/node_modules/111")).isTrue()
//                assertThat(rule.matches("aaa/bbb/node_modules/111/222")).isTrue()
//            }
//            it("should be case insensitive") {
//                assertThat(rule.matches("aaa/bbb/NoDe_MOduLeS/111/222")).isTrue()
//            }
        }
        describe("not match") {
            //            it("should not match paths which doesn't contain the rule") {
//                assertThat(rule.matches("aaanode_modules")).isFalse()
//                assertThat(rule.matches("./node_modules111")).isFalse()
//            }
        }
    }
//    describe("root path rules") {
//        val rule = Rule("/node_modules")
//        describe("match") {
//            it("should match file paths starts with the rule") {
//                assertThat(rule.matches("node_modules")).isTrue()
//                assertThat(rule.matches("/node_modules/")).isTrue()
//                assertThat(rule.matches("/node_modules/111")).isTrue()
//            }
//            it("should be case insensitive") {
//                assertThat(rule.matches("/NoDe_MOduLeS/111/222")).isTrue()
//            }
//        }
//        describe("not match") {
//            it("should not match paths which contain the rule but not from the root") {
//                assertThat(rule.matches("node_modules111")).isFalse()
//                assertThat(rule.matches("/aaa/node_modules")).isFalse()
//            }
//        }
//    }
    describe("wild chars") {
        //        describe("*") {
//            val rule = Rule("node_*111")
//            it("should match with wild char *") {
//                assertThat(rule.matches("node_111")).isTrue()
//                assertThat(rule.matches("node_aaa111")).isTrue()
//                assertThat(rule.matches("NODE_bbb111")).isTrue()
//            }
//            it("should not match if the path is not match") {
//                assertThat(rule.matches("node_modules")).isFalse()
//                assertThat(rule.matches("node_aaa/111")).isFalse()
//            }
//        }
//        describe("**") {
//            val rule = Rule("node/**/*")
//            it("should match with wild char *") {
//                assertThat(rule.matches("node_111")).isTrue()
//                assertThat(rule.matches("node_aaa111")).isTrue()
//                assertThat(rule.matches("NODE_bbb111")).isTrue()
//            }
//            it("should not match if the path is not match") {
//                assertThat(rule.matches("node_modules")).isFalse()
//                assertThat(rule.matches("node_aaa/111")).isFalse()
//            }
//        }
    }
})
