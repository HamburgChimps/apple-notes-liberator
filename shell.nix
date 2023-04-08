{ pkgs ? import <nixpkgs> {} }:

let
  apple-notes-liberator = pkgs.fetchurl {
    url = "https://github.com/HamburgChimps/apple-notes-liberator/releases/download/v1.0.4/apple-notes-liberator.jar";
    sha256="sha256-MYeIcgcTJwDoZOPUci+cxxSV6VRZY4ij67QRelsC6bM=";
  };
in pkgs.mkShell {
  buildInputs = with pkgs; [
    pkgs.openjdk19
  ];

    shellHook = ''
      cp ${apple-notes-liberator} .
      alias apple-notes-liberator='java -jar ${apple-notes-liberator}'
      echo "Usage: apple-notes-liberator"
    '';
}
