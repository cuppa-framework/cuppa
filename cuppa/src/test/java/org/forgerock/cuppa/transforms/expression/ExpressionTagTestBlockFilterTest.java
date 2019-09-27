/*
 * Copyright 2018 ForgeRock AS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.forgerock.cuppa.transforms.expression;

import static java.util.Collections.emptySet;
import static org.forgerock.cuppa.model.TestBlockType.ROOT;
import static org.forgerock.cuppa.model.TestBlockType.WHEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.model.TagsOption;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.model.TestBlockBuilder;
import org.forgerock.cuppa.model.TestBuilder;
import org.forgerock.cuppa.transforms.ExpressionTagTestBlockFilter;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;


/**
 * Tests the {@link ExpressionTagTestBlockFilter} class.
 */
public class ExpressionTagTestBlockFilterTest {

    private TestBlock root;

    @BeforeTest
    public void before() {
        TestBlock child = new TestBlockBuilder()
                .setType(WHEN)
                .setTestClass(ExpressionTagTestBlockFilterTest.class)
                .setTests(getTests("child-tag "))
                .setDescription("child description")
                .setOptions(createOption("child-tag"))
                .build();
        root = new TestBlockBuilder()
                .setType(ROOT)
                .setTestClass(ExpressionTagTestBlockFilterTest.class)
                .setTests(getTests(""))
                .setDescription("root description")
                .setTestBlocks(Collections.singletonList(child)).build();
    }

    private static List<Test> getTests(String tags) {
        List<Test> tests = new ArrayList<>();
        tests.add(buildNormalTest(tags + "other", createOption("other")));
        tests.add(buildNormalTest(tags + "ui", createOption("ui")));
        tests.add(buildNormalTest(tags + "ui other", createOption("ui", "other")));
        tests.add(buildNormalTest(tags + "smoke", createOption("smoke")));
        tests.add(buildNormalTest(tags + "smoke other", createOption("smoke", "other")));
        return tests;
    }

    private static Test buildNormalTest(String description, Options options) {
        return new TestBuilder()
                .setDescription(description)
                .setFunction(Optional.empty())
                .setTestClass(ExpressionTagTestBlockFilterTest.class)
                .setOptions(options).build();
    }

    @org.testng.annotations.Test
    public void noTagsReturnAll() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(), ""));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 10);
    }

    @org.testng.annotations.Test
    public void emptyIntersectionOf2GroupsIsEmpty() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "and(ui,smoke)"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertTrue(testsDescription.isEmpty());
    }


    @org.testng.annotations.Test
    public void intersectionOf2GroupsSuccess() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "and(child-tag,smoke)"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 2);
        Assert.assertTrue(testsDescription.contains("child-tag smoke"));
        Assert.assertTrue(testsDescription.contains("child-tag smoke other"));
    }

    @org.testng.annotations.Test
    public void intersectionOf2GroupsWithNOTSuccess() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "not(and(child-tag,smoke))"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 8);
        Assert.assertTrue(testsDescription.contains("child-tag ui"));
        Assert.assertTrue(testsDescription.contains("ui"));
        Assert.assertTrue(testsDescription.contains("child-tag ui other"));
        Assert.assertTrue(testsDescription.contains("ui other"));
        Assert.assertTrue(testsDescription.contains("smoke other"));
        Assert.assertTrue(testsDescription.contains("other"));
        Assert.assertTrue(testsDescription.contains("smoke"));
        Assert.assertTrue(testsDescription.contains("child-tag other"));
    }

    @org.testng.annotations.Test
    public void notAGroupSuccess() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "not(smoke)"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 6);
        Assert.assertTrue(testsDescription.contains("child-tag ui"));
        Assert.assertTrue(testsDescription.contains("ui"));
        Assert.assertTrue(testsDescription.contains("child-tag ui other"));
        Assert.assertTrue(testsDescription.contains("ui other"));
        Assert.assertTrue(testsDescription.contains("other"));
        Assert.assertTrue(testsDescription.contains("child-tag other"));
    }

    @org.testng.annotations.Test
    public void intersectionOf2GroupsWithOperatorUppercaseSuccess() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "AND(child-tag,smoke)"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 2);
        Assert.assertTrue(testsDescription.contains("child-tag smoke"));
        Assert.assertTrue(testsDescription.contains("child-tag smoke other"));
    }

    @org.testng.annotations.Test
    public void intersectionOf3GroupsSuccess() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "and(child-tag,smoke,other)"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 1);
        Assert.assertTrue(testsDescription.contains("child-tag smoke other"));
    }

    @org.testng.annotations.Test
    public void intersectionOf2GroupsAndIncludeOf1Success() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "or(ui,and(child-tag,smoke))"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 6);
        Assert.assertTrue(testsDescription.contains("child-tag smoke"));
        Assert.assertTrue(testsDescription.contains("child-tag smoke other"));
        Assert.assertTrue(testsDescription.contains("child-tag ui"));
        Assert.assertTrue(testsDescription.contains("ui"));
        Assert.assertTrue(testsDescription.contains("child-tag ui other"));
        Assert.assertTrue(testsDescription.contains("ui other"));
    }

    @org.testng.annotations.Test
    public void intersectionOf3GroupsAndIncludeOf1Success() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "or(ui,and(child-tag,smoke,other))"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 5);
        Assert.assertTrue(testsDescription.contains("child-tag smoke other"));
        Assert.assertTrue(testsDescription.contains("ui"));
        Assert.assertTrue(testsDescription.contains("child-tag ui"));
        Assert.assertTrue(testsDescription.contains("ui other"));
        Assert.assertTrue(testsDescription.contains("child-tag ui other"));
    }

    @org.testng.annotations.Test
    public void intersectionOf2GroupsAnd2IncludeOf1Success() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "or(ui,and(child-tag,smoke),other)"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 9);
        Assert.assertTrue(testsDescription.contains("child-tag smoke"));
        Assert.assertTrue(testsDescription.contains("child-tag smoke other"));
        Assert.assertTrue(testsDescription.contains("child-tag ui"));
        Assert.assertTrue(testsDescription.contains("ui"));
        Assert.assertTrue(testsDescription.contains("child-tag ui other"));
        Assert.assertTrue(testsDescription.contains("ui other"));
        Assert.assertTrue(testsDescription.contains("smoke other"));
        Assert.assertTrue(testsDescription.contains("other"));
        Assert.assertTrue(testsDescription.contains("child-tag other"));
    }

    @org.testng.annotations.Test
    public void intersectionOf2GroupsAnd2IncludeOf1WithSpacesSuccess() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "or(ui , and ( child-tag,  smoke),other  )"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 9);
        Assert.assertTrue(testsDescription.contains("child-tag smoke"));
        Assert.assertTrue(testsDescription.contains("child-tag smoke other"));
        Assert.assertTrue(testsDescription.contains("child-tag ui"));
        Assert.assertTrue(testsDescription.contains("ui"));
        Assert.assertTrue(testsDescription.contains("child-tag ui other"));
        Assert.assertTrue(testsDescription.contains("ui other"));
        Assert.assertTrue(testsDescription.contains("smoke other"));
        Assert.assertTrue(testsDescription.contains("other"));
        Assert.assertTrue(testsDescription.contains("child-tag other"));
    }

    @org.testng.annotations.Test(expectedExceptions = IllegalArgumentException.class)
    public void missingRightParenthesisThrowException() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "or(ui,and(child-tag,smoke),other"));
        filter.apply(root);
    }

    @org.testng.annotations.Test(expectedExceptions = IllegalArgumentException.class)
    public void missingOperatorThrowException() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "or(ui,(child-tag,smoke),other)"));
        filter.apply(root);
    }

    @org.testng.annotations.Test(expectedExceptions = IllegalArgumentException.class)
    public void unknownOperatorThrowException() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "unknown(ui,(child-tag,smoke),other)"));
        filter.apply(root);
    }

    @org.testng.annotations.Test(expectedExceptions = IllegalArgumentException.class)
    public void notSingleRootThowAnException() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "or(ui),and(other)"));
        filter.apply(root);
    }

    @org.testng.annotations.Test
    public void emptyGroupisValid() throws Exception {
        ExpressionTagTestBlockFilter filter = new ExpressionTagTestBlockFilter(new Tags(emptySet(), emptySet(),
                "and()"));
        TestBlock testBlocks = filter.apply(root);

        List<String> testsDescription = getTestsDescription(testBlocks);
        Assert.assertEquals(testsDescription.size(), 10);
    }

    private List<String> getTestsDescription(TestBlock testBlock) {
        Set<String> subTestsDescription = testBlock.testBlocks.stream()
                .flatMap(tb -> getTestsDescription(tb).stream())
                .collect(Collectors.toSet());

        Set<String> description = testBlock.tests.stream().map(t -> t.description).collect(Collectors.toSet());

        return union(subTestsDescription, description);
    }

    private static Options createOption(String... tags) {
        return Options.EMPTY.set(new TagsOption(new HashSet<>(Arrays.asList(tags))));
    }

    private <T> List<T> union(Collection<T> list1, Collection<T> list2) {
        Set<T> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }
}
