import 'package:flutter/material.dart';

import 'package:flutter_native_web/flutter_native_web.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/foundation.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {

  WebController webController;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {

    FlutterNativeWeb flutterWebView = new FlutterNativeWeb(
      onWebCreated: onWebCreated,
      gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>[
                        Factory<OneSequenceGestureRecognizer>(
                          () => TapGestureRecognizer(),
                        ),
                      ].toSet(),
    );

    return new MaterialApp(
      home: new Scaffold(
        appBar: null,
        body: Builder(builder: (BuildContext context) {
          return Stack(children: <Widget>[
            flutterWebView,
          ]);
        }),
        )
    );
  }

  void onWebCreated(webController) {
    this.webController = webController;
    this.webController.loadUrl("https://www.gamezop.com/g/Hk2yhp7cCH?id=UGNYn1MFQ&src=gamezop-website");
    this.webController.onPageStarted.listen((url) =>
        print("Loading $url")
    );
    this.webController.onPageFinished.listen((url) =>
        print("Finished loading $url")
    );
  }
}
