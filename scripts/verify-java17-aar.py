#!/usr/bin/env python3
"""Check every packaged class and compile a real consumer using JDK 17.

Usage: JAVA_HOME=/path/to/jdk17 python3 scripts/verify-java17-aar.py library.aar android.jar
Works with both the local release AAR and the artifact downloaded from Maven Central.
"""

import argparse
import io
import os
from pathlib import Path
import struct
import subprocess
import tempfile
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("aar", type=Path)
    parser.add_argument("android_jar", type=Path)
    args = parser.parse_args()
    javac = Path(os.environ["JAVA_HOME"]) / "bin" / "javac"
    version = subprocess.check_output([str(javac), "-version"], text=True).strip()
    if not version.startswith("javac 17."):
        raise SystemExit(f"This consumer test requires JDK 17, found {version}")
    if not args.android_jar.is_file():
        raise SystemExit(f"Missing Android SDK jar: {args.android_jar}")

    with tempfile.TemporaryDirectory(prefix="dotsindicator-java17-") as directory:
        work = Path(directory)
        jars = []
        majors = []
        with zipfile.ZipFile(args.aar) as aar:
            if "classes.jar" not in aar.namelist():
                raise SystemExit("AAR does not contain classes.jar")
            for name in aar.namelist():
                if name != "classes.jar" and not (name.startswith("libs/") and name.endswith(".jar")):
                    continue
                data = aar.read(name)
                jar = work / f"library-{len(jars)}.jar"
                jar.write_bytes(data)
                jars.append(jar)
                with zipfile.ZipFile(io.BytesIO(data)) as archive:
                    for entry in archive.namelist():
                        if not entry.endswith(".class"):
                            continue
                        header = archive.read(entry)[:8]
                        magic, minor, major = struct.unpack(">IHH", header)
                        if magic != 0xCAFEBABE or major > 61 or minor == 65535:
                            raise SystemExit(f"Not Java 17 compatible: {name}!{entry} ({major}.{minor})")
                        majors.append(major)
        if not majors:
            raise SystemExit("AAR contains no class files")
        print(f"Verified {len(majors)} classes; highest major version: {max(majors)} (Java {max(majors) - 44})", flush=True)
        source = work / "Java17Consumer.java"
        source.write_text("""import android.content.Context;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

final class Java17Consumer {
    void useLibrary(Context context) {
        new DotsIndicator(context).setDotsColor(0xff00ff00);
        new SpringDotsIndicator(context).setDotsColor(0xff00ff00);
        new WormDotsIndicator(context).setDotsColor(0xff00ff00);
    }
}
""")
        classpath = os.pathsep.join(str(p) for p in [*jars, args.android_jar.resolve()])
        subprocess.run([str(javac), "--release", "17", "-proc:none", "-classpath", classpath,
                        "-d", str(work / "consumer"), str(source)], check=True)
        print(f"PASS: release AAR consumer compiled with {version}")


if __name__ == "__main__":
    main()
