package com.speakerboxlite.router.sample.base

import com.speakerboxlite.router.RouteControllerVMC
import com.speakerboxlite.router.sample.di.AppComponent

typealias RouteControllerApp<Path, VM, V> = RouteControllerVMC<Path, VM, V, AppComponent>
