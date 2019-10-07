/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.litho;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ComponentsTestRunner.class)
public class TransitionUtilsTest {

  private static final String GLOBAL_KEY = "global-key";
  private static final String TRANSITION_KEY = "transition-key";
  private static final String TRANSITION_OWNER_KEY = "transition-owner-key";

  private InternalNode node;

  @Before
  public void before() {
    Component tailComponent = mock(Component.class);
    node = mock(InternalNode.class);
    when(node.getTransitionKey()).thenReturn(TRANSITION_KEY);
    when(node.hasTransitionKey()).thenReturn(true);
    when(node.getTransitionKeyType()).thenReturn(Transition.DEFAULT_TRANSITION_KEY_TYPE);
    when(node.getTransitionOwnerKey()).thenReturn(TRANSITION_OWNER_KEY);
    when(node.getTailComponent()).thenReturn(tailComponent);
    when(tailComponent.getGlobalKey()).thenReturn(GLOBAL_KEY);
  }

  @Test
  public void noTransitionKeyTest() {
    when(node.hasTransitionKey()).thenReturn(false);
    TransitionId id = TransitionUtils.createTransitionId(node);
    assertThat(id).isNotNull();
    assertThat(id.mType).isEqualTo(TransitionId.Type.AUTOGENERATED);
  }

  @Test
  public void defaultTransitionKeyTest() {
    TransitionId id = TransitionUtils.createTransitionId(node);
    assertThat(id).isNotNull();
    assertThat(id.mType).isEqualTo(TransitionId.Type.SCOPED);
    assertThat(id.mReference).isEqualTo(TRANSITION_KEY);
    assertThat(id.mExtraData).isEqualTo(TRANSITION_OWNER_KEY);
  }

  @Test
  public void globalTransitionKeyTest() {
    when(node.getTransitionKeyType()).thenReturn(Transition.TransitionKeyType.GLOBAL);
    TransitionId id = TransitionUtils.createTransitionId(node);
    assertThat(id).isNotNull();
    assertThat(id.mType).isEqualTo(TransitionId.Type.GLOBAL);
    assertThat(id.mReference).isEqualTo(TRANSITION_KEY);
  }

  @Test
  public void localTransitionKeyTest() {
    when(node.getTransitionKeyType()).thenReturn(Transition.TransitionKeyType.LOCAL);
    TransitionId id = TransitionUtils.createTransitionId(node);
    assertThat(id).isNotNull();
    assertThat(id.mType).isEqualTo(TransitionId.Type.SCOPED);
    assertThat(id.mReference).isEqualTo(TRANSITION_KEY);
    assertThat(id.mExtraData).isEqualTo(TRANSITION_OWNER_KEY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void unhandledTransitionKeyTypeTest() {
    when(node.getTransitionKeyType()).thenReturn(null);
    TransitionUtils.createTransitionId(node);
  }
}