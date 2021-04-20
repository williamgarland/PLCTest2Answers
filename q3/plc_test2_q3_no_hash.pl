sub main {
    my @people = ();

    for (my $i = 0; $i < 10000; $i++) {
        my $char1 = 65 + int rand(91 - 65);
        my $char2 = 65 + int rand(91 - 65);
        my $char3 = 65 + int rand(91 - 65);
        my $name = chr($char1) . chr($char2) . chr($char3);
        my $age = int rand(100);

        my $index = arrayGet(@people, $name);
        if ($index != -1) {
            $people[$index + 1] = $age;
        } else {
            push(@people, $name);
            push(@people, $age);
        }
    }

    $size = scalar @people;
    print "$size\n";

    for (my $i = 0; $i < $size; $i += 2) {
        my $name = $people[$i];
        my $age = $people[$i + 1];
        print "$name => $age\n";
    }
}

sub arrayGet {
    @arr = $_[0];
    $name = $_[1];

    for (my $i = 0; $i < scalar @arr; $i += 2) {
        if ($arr[$i] == $name) {
            return $i;
        }
    }

    return -1;
}

main();