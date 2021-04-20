my %people = ();

for (my $i = 0; $i < 10000; $i++) {
    my $char1 = 65 + int rand(91 - 65);
    my $char2 = 65 + int rand(91 - 65);
    my $char3 = 65 + int rand(91 - 65);
    my $name = chr($char1) . chr($char2) . chr($char3);
    my $age = int rand(100);
    $people{$name} = $age;
}

while (($k, $v) = each %people) {
    print "$k => $v\n";
}