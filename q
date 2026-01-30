[33mcommit 294e9aeb51ed23a65ff8f1edf02571122afd8935[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32mToc-chapters-jumping[m[33m)[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Jan 13 15:13:45 2026 +0700

    add: pagination functionality.

[33mcommit c67f38bac2197462ea2efedafd3483b8a094d3e8[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Jan 12 16:06:23 2026 +0700

    add: tap left/right to navigate

[33mcommit f317329bb6ac345ad8bf6d574614197b447f092a[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Fri Jan 9 16:06:45 2026 +0700

    add: is-loading indicator for library screen

[33mcommit 4dc8ec60703cfab2a278eb7f51eb0ac4cfcea9cb[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Fri Jan 9 15:33:45 2026 +0700

    update: update BookCard with a delete button

[33mcommit 4a0537f71853f5d85f0ee94308f886277aa0e34c[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Thu Jan 8 13:04:09 2026 +0700

    add: add button interface for deleting book feature.

[33mcommit f36427ad1049fffa90bf4c5d6984fd26524606b0[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Thu Jan 8 12:15:30 2026 +0700

    add: delete book feature.

[33mcommit fff56d0e4eae9ef57fbded6a6278f9e7bf7553ea[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Jan 7 13:43:22 2026 +0700

    fix: adding book from the same file creates duplicate copy

[33mcommit b985515d9b865fce759e8938b509b7e1d2c96d73[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Jan 6 15:23:41 2026 +0700

    make the logic for finding cover image more robust

[33mcommit 7828edbdc6e7ab0fd53836dc25329661920fb265[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Jan 6 15:00:12 2026 +0700

    add parsing logic for epub2 format

[33mcommit 527ec0905c5763aaaf27f797565d091671040636[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Dec 29 18:21:09 2025 +0700

    persistent reading screen's settings

[33mcommit 82bec21e26409a4c0ec24a5cdc90d6b8d58c2088[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Dec 29 16:11:59 2025 +0700

    implement reading screen's settings customization feature

[33mcommit ef9ffbf8580ccce34f669c87911de06ff1b71716[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Thu Dec 25 20:39:16 2025 +0700

    add saving last read position feature

[33mcommit 5d191a0c371ed6528632e7cf7c46c9d0983fcfd6[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Dec 24 12:53:14 2025 +0700

    fix ToC chapters jumping behavior, no more race condition

[33mcommit 21e17ba1c2ba3645342e467ad06db8124a1edf3d[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Dec 22 15:05:33 2025 +0700

    Implement "click ToC links to jump" feature.

[33mcommit 32e2a84452b9d1ab584cf148bc5210825f950836[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Thu Oct 16 15:18:11 2025 +0700

    feat: Implement Table of Contents (TOC) navigation
    
    This commit introduces a Table of Contents (TOC) feature in the reader screen, allowing users to view and navigate through the book's chapters.
    
    Changes include:
    - **ReaderScreen:**
        - A `ReaderTopBar` and `ReaderBottomBar` are introduced, which appear/disappear on tap.
        - A TOC icon in the top bar toggles the visibility of a new `TableOfContentsDropdown` component.
        - The TOC dropdown displays the book's chapter list, enabling navigation to a selected chapter.
    - **ReaderViewModel:**
        - New state `isTocVisible` is added to manage the TOC's visibility.
        - `toggleTocVisibility` and `navigateToChapterByToc` functions are implemented to handle TOC interactions.

[33mcommit 7bb8ccd0cbcc5663885495cbc2db9983ee7dda25[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Fri Oct 10 17:12:44 2025 +0700

    \display cover images in LibraryScreen

[33mcommit 5f7cbb22040efb262f88c29bcbab40bbaccd6d13[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Fri Oct 10 16:11:27 2025 +0700

    \display cover images in LibraryScreen

[33mcommit 7167aff28e91f93c3b2a65a92cff842be6eade76[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 30 14:27:12 2025 +0700

    \display cover images in LibraryScreen

[33mcommit 95542bb22c5f309f62384db3053aab6a69c99b42[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Thu Sep 25 18:08:47 2025 +0700

    "Add cover image for epub display"

[33mcommit ef04b66fbcdab9573223005849a53c96cfd22183[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Sep 24 15:28:08 2025 +0700

    Enhance Book model and entity with date and progress tracking fields
    
    This commit introduces new fields to the `Book` model and `BookEntity` to track when a book was added, when it was last read, and the overall reading progress.
    
    Specifically, the following fields have been added:
    - `dateAdded`: Timestamp for when the book was added.
    - `lastReadDate`: Timestamp for the last time the book was read.
    - `readProgress`: A float value representing the percentage of the book read.
    
    These changes are reflected in `BookRepositoryImpl` to handle the new fields during book creation and retrieval.
    
    In `ReaderViewModel`, the `try-catch` block within `loadBookContent` was removed as it was deemed redundant with existing error handling.

[33mcommit e9623dea896a09fa8869d67e22dcc18e3263f6ff[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Sep 24 14:22:50 2025 +0700

    pass bookId as argument for navigating to ReaderScreen

[33mcommit 4720b4e2c7f3f932cbea92c90c267be0230abdb8[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Sep 24 11:38:47 2025 +0700

    create ReaderScreen and ReaderViewModel for displaying book content

[33mcommit e78b048b0c1fb2f0082d8497d6bc519c8e24d6f1[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Sep 24 11:38:29 2025 +0700

    Update Book models and navigation for ReaderScreen integration

[33mcommit cd4b0f37df928ef65bfed409a1d070de575b114b[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 23 12:19:43 2025 +0700

    Refactor EpubParserService into EpubParser and BookFileDataSource
    
    The `EpubParserService` class has been removed. Its functionality for parsing EPUB files is now directly integrated into the `EpubParser` class.
    
    The `BookFileDataSource` now handles saving book files to app storage and extracting EPUB content. The `saveBookToAppStorage` function in `BookFileDataSource` has been made synchronous.
    
    The `BookRepositoryImpl` has been updated to use `EpubParser` directly instead of the removed `EpubParserService`.
    
    The `LibraryViewModel`'s import for `Outcome` has been corrected.

[33mcommit 2fd97c736b63bbd78dbebd6d6bc0e26213f562fc[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 22 20:26:45 2025 +0700

    Optimize codebase

[33mcommit 02d9d6780ea5d345f3dfe0347711f3b684844855[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 22 15:50:31 2025 +0700

    Update EpubParserService to use Dispatchers.Default for parsing.
    Update BookRepositoryImpl to use Dispatchers.IO for addAndExtractBook.

[33mcommit 22f89951c539b31a32727f6304d02f1bbcaaecb7[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 22 14:45:04 2025 +0700

    move helper classes to utils package

[33mcommit 43ba55397c2249ee901252e9831aa2deb3e0bf0b[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 22 14:36:13 2025 +0700

    update codebase to minSdk 23

[33mcommit a36c2d7ff366566522317f4a4ca1caf4a32bf71d[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Fri Sep 12 17:06:30 2025 +0700

    apply kotlin code styling guidelines to project

[33mcommit 3fb005c74c871b69e9e686214e0e253978b3c7ef[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Fri Sep 12 12:29:56 2025 +0700

    add add book functionality

[33mcommit 243758f94ace0de34da15a047e0a8a56b514cd9d[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Sep 10 15:03:47 2025 +0700

    finish epub parsing logic

[33mcommit 7c97a0a0f910bd4c610c04e66f7e1707e068ff9f[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Sep 10 12:30:36 2025 +0700

    add ChapterParser for chapter parsing logic

[33mcommit 8ac2a365b3c9fc4b67fb777cb15130534714a8d4[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Wed Sep 10 12:16:27 2025 +0700

    add toc parser for table of contents parsing logic

[33mcommit 7e24bed54b679624b803234f074e373ffef124ab[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 13:41:36 2025 +0700

[33mcommit c042bee519d1d1ff74aeab5818d8e6f9dff88a5c[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 13:32:05 2025 +0700

    create method for getting zipped files in epub archive

[33mcommit cfd6c9fb8f0e6f6fdec0747320dbfcdddc005e73[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 13:14:16 2025 +0700

    fill book domain model class

[33mcommit 6ddd6037b04dfe878b5187cd796c7d6562080f2f[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 13:08:47 2025 +0700

    setup book dao and app database dependencies's module

[33mcommit 3ccfe03c44e2a73d9e9b38534236d82249b035e0[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 13:04:44 2025 +0700

    setup app database

[33mcommit e91c80144ea6d5d19ddba495bfd2336fbfc1a617[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 12:57:47 2025 +0700

    add basic query methods for book dao

[33mcommit ae156c2edd205e1a2ae8879f9405ead7723ef1ff[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 12:54:24 2025 +0700

    create essential classes, create book entity

[33mcommit 5c75c902294371607f1cadd7a3e9e68a5f31d615[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Tue Sep 9 12:31:00 2025 +0700

[33mcommit 5ece7065b81b97c76665676111529a0c9c8464de[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 8 16:07:45 2025 +0700

    create LibraryViewModel and add function addBook

[33mcommit bac8c6716efa792334ead084ac9b818f4d3ba74c[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 8 15:43:52 2025 +0700

    add file picker functionality to LibraryScreen.kt

[33mcommit 3f33b8ac03c92a0a0b61802970a758b177fe23b4[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 8 15:11:54 2025 +0700

    Add LibraryScreen with a button to select epub file

[33mcommit b9d5396bf38c061fa77f8139387d1cfbfee27b47[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 8 15:04:08 2025 +0700

    create AppNavGraph to define app navigation flow

[33mcommit 1aadcf61d824ee3ef0e2b600e4832fb524994ddb[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 8 14:33:59 2025 +0700

    add essential denpendencies

[33mcommit 6f64d0047af19ef462eea881c8f3edd2da629dda[m
Author: hoang <hoangle.ntkh@gmail.com>
Date:   Mon Sep 8 12:39:22 2025 +0700

    initial project setup
