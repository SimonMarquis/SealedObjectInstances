/*
 * Copyright (C) 2023 Simon Marquis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tschuchort.compiletesting

import fr.smarquis.sealed.SealedObjectInstancesProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
fun compile(
    vararg source: SourceFile,
    configure: KotlinCompilation.() -> Unit = {},
): JvmCompilationResult = KotlinCompilation().apply {
    sources = source.asList()
    symbolProcessorProviders = listOf(SealedObjectInstancesProcessorProvider())
    kspWithCompilation = true
    inheritClassPath = true
    configure()
}.compile()
