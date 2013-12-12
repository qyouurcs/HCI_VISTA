HCI_VISTA
=========

* Name: Vista
* Website: https://sites.google.com/site/hcivista/
* Source Codes: https://github.com/qyouurcs/HCI_VISTA
* Recommanded Platforms: Newest Version of Android (4.2 and 4.4 recommended)

What Is It?:
==================================

Have you ever been in this kind of situations, where you see items you want to purchase but have no clue what exact words you should use to put into a search engine, or you know imcomplete information such as the brand name, but there are so many items you can find under that brand that you are still not able to find your exact item? 

Well, Vista comes to the rescue!

Vista is an Android App that you use for shopping. The main features of Vista is two-folds: 1). Image Based Search Engine and 2). Automatically Object Detection and Voice Control. 

Don't know the name of the brand or the item? No problem, just take a picture, paste a URL! Don't want to type that many characters? No problem, just say what you want! Vista will do everything for you.

Features:
==================================
1. Various input methods including taking pictures, selecting pictures from gallery, and pasting URLs;
2. Easy gesture control for drawing, scaling, moving, deleting and selecting bounding box;
3. Automatically segmentation using saliency detection and clustering, saving touble from moving fingers too often;
4. Pre-built up object detectors integrating with voice control command, just say what you're looking for;
5. Item retrivial based on edges, colors, and textures; 
6. Results including a list of most similar items in our database are returned from our server.

How Do I Use It? 
==================================
When you open Vista, you will see three option buttons each indicating to paste a URL, take a picture and select one from your gallery. By choosing which you want as a method of an input, you can draw a bounding box on the selected picture. Then you can conduct scaling, moving, selecting and canceling activity just using your fingers.

Additional feature is voice controlled object detector: you can click on the microphone icon in the right up corner and say the object you want to detect such as "people" and "bag", and our app will automatically detect objects according to what your command. You can also say "segmentation" for automatically image segmentation to get the most salient object. All of these commands will lead you to a bounding box which can be edited as stated before.

After you have a bounding box, you can click submit to upload this area to our server for item retrieval, and we will return you a list of items we find in our database which are the most similar (same if we could) as well as their links, prices and all the other information you may need.

Version Updates/Log:
==================================
* 11-01: Paper Prototype, Database Design
* 11-10: Database set up (images from UR bookstore under all categories) 
* 11-12: First design of UI, server setup using Amazon EC2 
* 11-14: Connecting to server, slow performance
* 11-16: Finger selected bounding box feature
* 11-20: Low-fidelity prototype
* 11-26: Human detector and handbag detector added
* 11-28: Saliency detection and kmeans implemented for auto image segmentation
* 11-30: Voice control Feature added
* 12-02: UI optimization, evaluation process

Contacts
==================================
Authors:
Quanzeng You, Lam Tran, Jianbo Yuan, Yukun Liu, and Jonathon Wong

For further information and inquiries about our app and source codes, please
feel free to contact Quanzeng You at <qyou2@cs.rochester.com>, Lam Tran at <ltran@cs.rochester.edu> or Jianbo Yuan at <jyuan10@ece.rochester.edu>. Please check out our website for new features and feedbacks are mostly welcome and appreciated.

Copyright (C) 2013.
