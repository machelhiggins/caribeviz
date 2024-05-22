/*
 * Copyright (c) 2002-2016 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.javascript.host.svg;

import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.CHROME;
import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.EDGE;
import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.FF;
import static com.gargoylesoftware.htmlunit.javascript.configuration.BrowserName.IE;

import com.gargoylesoftware.htmlunit.javascript.configuration.JsxClass;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxConstant;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxConstructor;
import com.gargoylesoftware.htmlunit.javascript.configuration.WebBrowser;
import com.gargoylesoftware.htmlunit.svg.SvgPattern;

/**
 * A JavaScript object for {@code SVGPatternElement}.
 *
 * @author Ahmed Ashour
 */
@JsxClass(domClass = SvgPattern.class)
public class SVGPatternElement extends SVGElement {

    /** Constant for {@code SVG_UNIT_TYPE_UNKNOWN}. */
    @JsxConstant({ @WebBrowser(FF), @WebBrowser(IE) })
    public static final int SVG_UNIT_TYPE_UNKNOWN = 0;
    /** Constant for {@code SVG_UNIT_TYPE_USERSPACEONUSE}. */
    @JsxConstant({ @WebBrowser(FF), @WebBrowser(IE) })
    public static final int SVG_UNIT_TYPE_USERSPACEONUSE = 1;
    /** Constant for {@code SVG_UNIT_TYPE_OBJECTBOUNDINGBOX}. */
    @JsxConstant({ @WebBrowser(FF), @WebBrowser(IE) })
    public static final int SVG_UNIT_TYPE_OBJECTBOUNDINGBOX = 2;

    /**
     * Creates an instance.
     */
    @JsxConstructor({ @WebBrowser(CHROME), @WebBrowser(FF), @WebBrowser(EDGE) })
    public SVGPatternElement() {
    }
}
